package model.xml

import java.nio.file._

import javax.inject._
import model.core.CoreConsts.{difficultyName, durationName}
import model.core._
import model.core.result.{CompleteResult, EvaluationResult}
import model.toolMains.{IdExerciseToolMain, ToolList, ToolState}
import model.xml.XmlConsts._
import model.xml.dtd.DocTypeDefParser
import model.{Consts, Difficulties, ExerciseState, MyYamlFormat, Points, SemanticVersion, User}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.JsString
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

trait XmlEvaluationResult extends EvaluationResult with JsonWriteable

trait XmlCompleteResult extends CompleteResult[XmlEvaluationResult]

@Singleton
class XmlToolMain @Inject()(val tables: XmlTableDefs)(implicit ec: ExecutionContext)
  extends IdExerciseToolMain("Xml", "xml") with FileUtils {

  // Result types

  override type ExType = XmlExercise

  override type CompExType = XmlCompleteExercise

  override type Tables = XmlTableDefs

  override type PartType = XmlExPart

  override type SolType = String

  override type DBSolType = XmlSolution

  override type R = XmlEvaluationResult

  override type CompResult = XmlCompleteResult

  override type ReviewType = XmlExerciseReview

  // Other members

  override val hasPlayground = true

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = XmlConsts

  override val exParts: Seq[XmlExPart] = XmlExParts.values

  // Forms

  override val compExForm: Form[XmlExercise] = null
  //    Form(mapping(
  //    "id" -> number,
  //    "title" -> nonEmptyText,
  //    "author" -> nonEmptyText,
  //    "text" -> nonEmptyText,
  //    "status" -> ExerciseState.formField,
  //    "grammarDescription" -> nonEmptyText,
  //    "sampleGrammar" -> nonEmptyText,
  //    "rootNode" -> nonEmptyText
  //  )(XmlExercise.apply)(XmlExercise.unapply))


  override def exerciseReviewForm(username: String, completeExercise: XmlCompleteExercise, exercisePart: XmlExPart): Form[XmlExerciseReview] = {

    val apply = (diffStr: String, dur: Option[Int]) =>
      XmlExerciseReview(username, completeExercise.ex.id, completeExercise.ex.semanticVersion, exercisePart, Difficulties.withNameInsensitive(diffStr), dur)

    val unapply = (cr: XmlExerciseReview) => Some((cr.difficulty.entryName, cr.maybeDuration))

    Form(
      mapping(
        difficultyName -> nonEmptyText,
        durationName -> optional(number(min = 0, max = 100))
      )(apply)(unapply)
    )
  }

  // Reading solution from requests, saving

  override protected def readSolution(user: User, exercise: XmlCompleteExercise, part: XmlExPart)(implicit request: Request[AnyContent]): Try[String] = request.body.asJson match {
    case None          => Failure(new Exception("Request body does not contain json!"))
    case Some(jsValue) => jsValue match {
      case JsString(solution) => Success(solution)
      case other              => Failure(new Exception("Wrong json content: " + other.toString))
    }
  }

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): XmlCompleteExercise = XmlCompleteExercise(
    XmlExercise(id, SemanticVersion(0, 1, 0), title = "", author = "", text = "", state, grammarDescription = "", rootNode = ""),
    sampleGrammars = Seq[XmlSampleGrammar](), sampleDocuments = Seq[XmlSampleDocument]())

  override def instantiateSolution(username: String, exercise: XmlCompleteExercise, part: XmlExPart, solution: String, points: Points, maxPoints: Points): XmlSolution =
    XmlSolution(username, exercise.ex.id, exercise.ex.semanticVersion, part, solution, points, maxPoints)

  private def getFirstSampleGrammar(completeEx: XmlCompleteExercise): Option[XmlSampleGrammar] = completeEx.sampleGrammars.toList match {
    case Nil       => None
    case head :: _ => Some(head)
  }

  // Yaml

  override val yamlFormat: MyYamlFormat[XmlCompleteExercise] = XmlExYamlProtocol.XmlExYamlFormat

  // Correction

  override protected def correctEx(user: User, solution: SolType, completeEx: XmlCompleteExercise, part: XmlExPart): Future[Try[XmlCompleteResult]] =
    Future(part match {
      case XmlExParts.DocumentCreationXmlPart => checkAndCreateSolDir(user.username, completeEx) flatMap { dir =>
        getFirstSampleGrammar(completeEx) map (_.sampleGrammar.toString) match {
          case None                 => Failure(new Exception("There is no grammar for exercise " + completeEx.ex.id))
          case Some(grammarToWrite) =>

            val grammarAndXmlTries: Try[(Path, Path)] = for {
              grammar <- write(dir, completeEx.ex.rootNode + ".dtd", grammarToWrite)
              xml <- write(dir, completeEx.ex.rootNode + "." + XML_FILE_ENDING, solution)
            } yield (grammar, xml)

            grammarAndXmlTries map { case (_, xml) =>
              val correctionResult = XmlCorrector.correctAgainstMentionedDTD(xml)
              XmlDocumentCompleteResult(solution, correctionResult)
            }
        }
      }

      case XmlExParts.GrammarCreationXmlPart =>

        val maybeSampleGrammar = completeEx.sampleGrammars.reduceOption((sampleG1, sampleG2) => {
          val dist1 = Java_Levenshtein.levenshteinDistance(solution, sampleG1.sampleGrammar.asString)
          val dist2 = Java_Levenshtein.levenshteinDistance(solution, sampleG2.sampleGrammar.asString)

          if (dist1 < dist2) sampleG1 else sampleG2
        })

        maybeSampleGrammar match {
          case None                => Failure[XmlCompleteResult](new Exception("Could not find a sample grammar!"))
          case Some(sampleGrammar) => Success(XmlGrammarCompleteResult(DocTypeDefParser.parseDTD(solution), sampleGrammar, completeEx))
        }
    })

  override def futureSampleSolutionForExerciseAndPart(id: Int, part: XmlExPart): Future[Option[String]] = futureCompleteExById(id) map {
    futureCompleteEx =>
      part match {
        case XmlExParts.GrammarCreationXmlPart  => futureCompleteEx flatMap getFirstSampleGrammar map (_.sampleGrammar.toString)
        case XmlExParts.DocumentCreationXmlPart => futureCompleteEx flatMap {
          _.sampleDocuments.toList match {
            case Nil       => None
            case head :: _ => Some(head.document)
          }
        }
      }
  }

  // Views

  override def renderExerciseEditForm(user: User, newEx: XmlCompleteExercise, isCreation: Boolean, toolList: ToolList): Html =
    views.html.idExercises.xml.editXmlExercise(user, newEx, isCreation, this, toolList)

  override def renderExercise(user: User, exercise: XmlCompleteExercise, part: XmlExPart, maybeOldSolution: Option[XmlSolution]): Html = {
    val oldSolutionOrTemplate = maybeOldSolution map (_.solution) getOrElse exercise.getTemplate(part)

    views.html.idExercises.xml.xmlExercise(user, this, exercise, oldSolutionOrTemplate, part)
  }

  override def playground(user: User): Html = views.html.idExercises.xml.xmlPlayground(user)

}