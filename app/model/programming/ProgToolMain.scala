package model.programming

import javax.inject._
import model.programming.ProgConsts._
import model.toolMains.{IdExerciseToolMain, ToolState}
import model.yaml.MyYamlFormat
import model.{Consts, ExerciseState, SemanticVersion, User}
import play.api.data.Form
import play.api.libs.json._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object ProgToolMain {

  val standardTestCount = 2

}

@Singleton
class ProgToolMain @Inject()(override val tables: ProgTableDefs)(implicit ec: ExecutionContext) extends IdExerciseToolMain(urlPart = "programming") {

  // Abstract types

  override type ExType = ProgExercise

  override type CompExType = ProgCompleteEx

  override type Tables = ProgTableDefs

  override type PartType = ProgExPart

  override type SolType = ProgSolution

  override type DBSolType = DBProgSolution

  override type R = ProgEvalResult

  override type CompResult = ProgCompleteResult

  // Other members

  override val toolname: String = "Programmierung"

  override val toolState: ToolState = ToolState.BETA

  override val consts: Consts = ProgConsts

  override val exParts: Seq[ProgExPart] = ProgExParts.values

  // TODO: create Form mapping ...
  override implicit val compExForm: Form[ProgExercise] = null

  private val implExtractorRegex = "# \\{12?\\}([\\S\\s]*)# \\{12?\\}".r

  private val actDiagExtractorRegex = "# \\{1?2\\}([\\S\\s]*)# \\{1?2\\}".r

  // Reading solution from requests

  override def readSolutionForPartFromJson(user: User, exercise: ProgCompleteEx, jsValue: JsValue, part: ProgExPart): Option[SolType] = jsValue.asObj flatMap { jsObj =>

    part match {
      case ProgExParts.TestdataCreation => for {
        testData <- jsObj.arrayField(testdataName, _.asObj flatMap (jsValue => readTestData(exercise.ex.id, exercise.ex.semanticVersion, jsValue, user)))
        language <- jsObj.stringField(languageName) map ProgLanguages.withNameInsensitive
      } yield ProgTestDataSolution.apply(testData, language)

      case ProgExParts.Implementation | ProgExParts.ActivityDiagram => for {
        solution <- jsObj.stringField(implementationName)
        language <- jsObj.stringField(languageName) map ProgLanguages.withNameInsensitive
      } yield ProgStringSolution(solution, language)

    }

  }

  private def readTestData(exId: Int, exSemVer: SemanticVersion, tdJsObj: JsObject, user: User): Option[CommitedTestData] = for {
    testId <- tdJsObj.intField(idName)
    inputAsJson <- tdJsObj.field(inputName)
    output <- tdJsObj.stringField(outputName)
  } yield CommitedTestData(testId, exId, exSemVer, inputAsJson, output, user.username, ExerciseState.RESERVED)

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): ProgCompleteEx = ProgCompleteEx(
    ProgExercise(id, SemanticVersion(0, 1, 0), title = "", author = "", text = "", state,
      folderIdentifier = "", base = "", functionname = "", indentLevel = 0, outputType = ProgDataTypes.STRING, baseData = None),
    inputTypes = Seq.empty, sampleSolutions = Seq.empty, sampleTestData = Seq.empty, maybeClassDiagramPart = None
  )

  override def instantiateSolution(username: String, exercise: ProgCompleteEx, part: ProgExPart,
                                   solution: ProgSolution, points: Double, maxPoints: Double): DBProgSolution =
    DBProgSolution(username, exercise.ex.id, exercise.ex.semanticVersion, part, solution.solution, solution.language, points, maxPoints)

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[ProgCompleteEx] = ProgExYamlProtocol.ProgExYamlFormat

  // Correction


  override def correctEx(user: User, sol: SolType, exercise: ProgCompleteEx, part: ProgExPart): Future[Try[ProgCompleteResult]] = {

    val (implementation, testData) = sol match {
      case ProgTestDataSolution(td, _) =>
        (exercise.sampleSolutions.head.solution, td)

      case ProgStringSolution(solution, _) => part match {
        case ProgExParts.Implementation => (implExtractorRegex.replaceFirstIn(exercise.ex.base, solution), exercise.sampleTestData)

        case ProgExParts.ActivityDiagram => (actDiagExtractorRegex.replaceFirstIn(exercise.ex.base, exercise.addIndent(solution)), exercise.sampleTestData)
      }
    }

    val correctionResult: Try[Future[Try[ProgCompleteResult]]] =
      ProgCorrector.correct(user, exercise, sol.language, implementation, testData, toolMain = this)

    correctionResult match {
      case Success(futureRes) => futureRes
      case Failure(error)     => Future(Failure(error))
    }
  }

  override def futureSampleSolutionForExerciseAndPart(id: Int, part: ProgExPart): Future[String] = part match {
    case ProgExParts.Implementation =>
      futureCompleteExById(id) map {
        case Some(exercise) => exercise.sampleSolutions.headOption.map(_.solution).getOrElse("No sample solution!")
        case None           => "No such exercise!"
      }
    case _                          => Future("TODO!")
  }

  // Views

  override def renderExercise(user: User, exercise: ProgCompleteEx, part: ProgExPart, maybeOldSolution: Option[DBProgSolution]): Html = part match {
    case ProgExParts.TestdataCreation =>
      val oldTestData: Seq[CommitedTestData] = maybeOldSolution.map(_.commitedTestData).getOrElse(Seq.empty)
      views.html.idExercises.programming.testDataCreation(user, exercise, oldTestData, this)

    case ProgExParts.Implementation =>
      val declaration: String = maybeOldSolution map (_.solution) map {
        case ptds: ProgTestDataSolution => ""
        case pss: ProgStringSolution    => pss.solution
      } getOrElse {
        //        FIXME: remove comments like '# {2}'!
        implExtractorRegex.findFirstMatchIn(exercise.ex.base) map (_.group(1).trim()) getOrElse exercise.ex.base
      }

      views.html.idExercises.programming.progExercise(user, this, exercise, declaration, ProgExParts.Implementation)

    case ProgExParts.ActivityDiagram =>
      // TODO: use old soluton!
      views.html.idExercises.umlActivity.activityDrawing.render(user, exercise, language = ProgLanguages.STANDARD_LANG, toolObject = this)
  }

  override def renderEditRest(exercise: ProgCompleteEx): Html = ???

}
