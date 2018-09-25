package model.xml

import java.nio.file._

import javax.inject._
import model.core.CoreConsts.{difficultyName, durationName}
import model.core._
import model.core.result.{CompleteResult, EvaluationResult}
import model.toolMains.{IdExerciseToolMain, ToolList, ToolState}
import model.xml.XmlConsts._
import model.xml.dtd.{DocTypeDef, DocTypeDefLine, DocTypeDefParser}
import model.{Consts, Difficulties, ExerciseState, MyYamlFormat, Points, SemanticVersionHelper, User}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.MessagesProvider
import play.api.libs.json.JsString
import play.api.mvc.{AnyContent, Request, RequestHeader}
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

  override type CompExType = XmlCompleteEx

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

  override val compExForm: Form[XmlCompleteEx] = XmlCompleteExerciseForm.format

  override def exerciseReviewForm(username: String, completeExercise: XmlCompleteEx, exercisePart: XmlExPart): Form[XmlExerciseReview] = {

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

  override protected def readSolution(user: User, exercise: XmlCompleteEx, part: XmlExPart)(implicit request: Request[AnyContent]): Try[String] = request.body.asJson match {
    case None          => Failure(new Exception("Request body does not contain json!"))
    case Some(jsValue) => jsValue match {
      case JsString(solution) => Success(solution)
      case other              => Failure(new Exception("Wrong json content: " + other.toString))
    }
  }

  // Other helper methods

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): XmlCompleteEx = XmlCompleteEx(
    XmlExercise(id, SemanticVersionHelper.DEFAULT, title = "", author, text = "", state, grammarDescription = "", rootNode = ""),
    samples = Seq(XmlSample(1, id, SemanticVersionHelper.DEFAULT, DocTypeDef(lines = Seq[DocTypeDefLine]()), "")))

  override def instantiateSolution(username: String, exercise: XmlCompleteEx, part: XmlExPart, solution: String, points: Points, maxPoints: Points): XmlSolution =
    XmlSolution(username, exercise.ex.id, exercise.ex.semanticVersion, part, solution, points, maxPoints)

  private def getFirstSample(completeEx: XmlCompleteEx): Option[XmlSample] = completeEx.samples.toList match {
    case Nil       => None
    case head :: _ => Some(head)
  }

  // Yaml

  override val yamlFormat: MyYamlFormat[XmlCompleteEx] = XmlExYamlProtocol.XmlExYamlFormat

  // Correction

  override protected def correctEx(user: User, solution: SolType, completeEx: XmlCompleteEx, part: XmlExPart): Future[Try[XmlCompleteResult]] =
    Future(part match {
      case XmlExParts.DocumentCreationXmlPart => checkAndCreateSolDir(user.username, completeEx) flatMap { dir =>
        getFirstSample(completeEx) map (_.sampleGrammar.asString) match {
          case None                 => Failure(new Exception(s"There is no grammar for exercise ${completeEx.ex.id}"))
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

        val maybeSampleGrammar = completeEx.samples.reduceOption((sampleG1, sampleG2) => {
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
      futureCompleteEx flatMap getFirstSample map { firstSample: XmlSample =>
        part match {
          case XmlExParts.GrammarCreationXmlPart  => firstSample.sampleGrammar.asString
          case XmlExParts.DocumentCreationXmlPart => firstSample.sampleDocument
        }
      }
  }

  // Views

  override def renderAdminExerciseEditForm(user: User, newEx: XmlCompleteEx, isCreation: Boolean, toolList: ToolList): Html =
    views.html.idExercises.xml.adminEditXmlExercise(user, newEx, isCreation, this, toolList)

  override def renderUserExerciseEditForm(user: User, newExForm: Form[XmlCompleteEx], isCreation: Boolean)
                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.xml.editXmlExerciseForm(user, newExForm, isCreation, this)

  override def renderExercise(user: User, exercise: XmlCompleteEx, part: XmlExPart, maybeOldSolution: Option[XmlSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {
    val oldSolutionOrTemplate = maybeOldSolution map (_.solution) getOrElse exercise.getTemplate(part)

    views.html.idExercises.xml.xmlExercise(user, this, exercise, oldSolutionOrTemplate, part)
  }

  override def renderExercisePreview(user: User, newExercise: XmlCompleteEx, saved: Boolean): Html =
    views.html.idExercises.xml.xmlNewExercise(user, newExercise, saved, this)

  override def playground(user: User): Html = views.html.idExercises.xml.xmlPlayground(user)

}