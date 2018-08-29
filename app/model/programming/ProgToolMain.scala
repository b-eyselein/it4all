package model.programming

import javax.inject._
import model.toolMains.{IdExerciseToolMain, ToolState}
import model.yaml.MyYamlFormat
import model.{Consts, ExerciseState, Points, SemanticVersion, User}
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object ProgToolMain {

  val standardTestCount = 2

}

@Singleton
class ProgToolMain @Inject()(override val tables: ProgTableDefs)(implicit ec: ExecutionContext)
  extends IdExerciseToolMain("Programmierung", "programming") {

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

  override val toolState: ToolState = ToolState.BETA

  override val consts: Consts = ProgConsts

  override val exParts: Seq[ProgExPart] = ProgExParts.values

  // TODO: create Form mapping ...
  override implicit val compExForm: Form[ProgExercise] = null

  private val implExtractorRegex = "# \\{12?\\}([\\S\\s]*)# \\{12?\\}".r

  private val actDiagExtractorRegex = "# \\{1?2\\}([\\S\\s]*)# \\{1?2\\}".r

  // Reading solution from requests

  override protected def readSolution(user: User, exercise: ProgCompleteEx, part: ProgExPart)(implicit request: Request[AnyContent]): Option[ProgSolution] =
    request.body.asJson flatMap { jsValue =>
      ProgSolutionJsonFormat(exercise, user).readProgSolutionFromJson(part, jsValue) match {
        case JsSuccess(solution, _) => Some(solution)
        case JsError(errors)        =>
          errors.foreach(println)
          None
      }
    }

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): ProgCompleteEx = ProgCompleteEx(
    ProgExercise(id, SemanticVersion(0, 1, 0), title = "", author = "", text = "", state,
      folderIdentifier = "", base = "", functionname = "", indentLevel = 0, outputType = ProgDataTypes.STRING, baseData = None),
    inputTypes = Seq.empty, sampleSolutions = Seq.empty, sampleTestData = Seq.empty, maybeClassDiagramPart = None
  )

  override def instantiateSolution(username: String, exercise: ProgCompleteEx, part: ProgExPart,
                                   solution: ProgSolution, points: Points, maxPoints: Points): DBProgSolution =
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
        case _: ProgTestDataSolution => ""
        case pss: ProgStringSolution => pss.solution
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
