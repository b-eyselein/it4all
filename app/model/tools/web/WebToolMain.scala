package model.tools.web

import better.files.File
import com.gargoylesoftware.htmlunit.ScriptException
import javax.inject._
import model._
import model.toolMains._
import model.tools.web.persistence.WebTableDefs
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.Logger
import play.api.data._
import play.api.i18n.MessagesProvider
import play.api.libs.json.JsString
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Try}

@Singleton
class WebToolMain @Inject()(val tables: WebTableDefs)(implicit ec: ExecutionContext) extends CollectionToolMain("Web", "web") {

  private val logger = Logger(classOf[WebToolMain])

  // Result types

  override type PartType = WebExPart

  override type ExType = WebExercise

  override type CollType = WebCollection


  override type SolType = WebSolution

  override type SampleSolType = WebSampleSolution

  override type UserSolType = WebUserSolution


  override type Tables = WebTableDefs

  override type ResultType = WebResult

  override type CompResultType = WebCompleteResult

  override type ReviewType = WebExerciseReview

  // Other members

  override val hasPlayground: Boolean = true

  override val toolState: ToolState = ToolState.LIVE

  override protected val exParts: Seq[WebExPart] = WebExParts.values

  override protected val completeResultJsonProtocol: WebCompleteResultJsonProtocol.type = WebCompleteResultJsonProtocol

  // Yaml, Html forms

  override protected val collectionYamlFormat: MyYamlFormat[WebCollection] = WebExYamlProtocol.WebCollectionYamlFormat
  override protected val exerciseYamlFormat  : MyYamlFormat[WebExercise]   = WebExYamlProtocol.WebExYamlFormat

  override val collectionForm: Form[WebCollection] = WebExerciseForm.collectionFormat
  override val exerciseForm  : Form[WebExercise]   = WebExerciseForm.exerciseFormat

  //  override def exerciseReviewForm(username: String, exercise: WebExercise, exercisePart: WebExPart): Form[WebExerciseReview] = Form(
  //    mapping(
  //      difficultyName -> Difficulties.formField,
  //      durationName -> optional(number(min = 0, max = 100))
  //    )
  //    (WebExerciseReview(username, exercise.id, exercise.semanticVersion, exercisePart, _, _))
  //    (wer => Some((wer.difficulty, wer.maybeDuration)))
  //  )

  // DB

  def writeWebSolutionFile(username: String, exerciseId: Int, part: WebExPart, content: String): Try[File] = Try {
    val target: File = solutionDirForExercise(username, exerciseId) / s"test.html"
    target.createFileIfNotExists(createParents = true).write(content)
  }

  override def futureMaybeOldSolution(user: User, exIdentifier: ExIdentifierType, part: WebExPart): Future[Option[UserSolType]] =
    super.futureMaybeOldSolution(user, exIdentifier, part) flatMap {
      case Some(solution) => Future.successful(Some(solution))
      case None           =>
        part match {
          case WebExParts.JsPart => super.futureMaybeOldSolution(user, exIdentifier, WebExParts.HtmlPart)
          case _                 => Future.successful(None)
        }
    }

  // Reading solution from request

  override protected def readSolution(user: User, collection: WebCollection, exercise: WebExercise, part: WebExPart)
                                     (implicit request: Request[AnyContent]): Option[WebSolution] =
    request.body.asJson match {
      case None          =>
        logger.error("Request body does not contain json!")
        None
      case Some(jsValue) => jsValue match {
        case JsString(solution) =>
          part match {
            case WebExParts.HtmlPart => Some(WebSolution(htmlSolution = solution, jsSolution = ""))
            case WebExParts.JsPart   => Some(WebSolution(htmlSolution = "", jsSolution = solution))
          }
        case other              =>
          logger.error("Wrong json content: " + other.toString)
          None
      }
    }

  // Other helper methods

  def getSolutionUrl(user: User, exerciseId: Int, part: WebExPart): String =
    s"http://localhost:9080/${user.username}/$exerciseId/test.html"

  override protected def exerciseHasPart(exercise: WebExercise, partType: WebExPart): Boolean = partType match {
    case WebExParts.HtmlPart => exercise.htmlTasks.nonEmpty
    case WebExParts.JsPart   => exercise.jsTasks.nonEmpty
  }

  override def instantiateCollection(id: Int, author: String, state: ExerciseState): WebCollection =
    WebCollection(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): WebExercise = WebExercise(
    id, SemanticVersionHelper.DEFAULT, title = "", author, text = "", state, htmlText = None, jsText = None,
    htmlTasks = Seq(
      HtmlTask(1, "", "", None, attributes = Seq[HtmlAttribute]())
    ),
    jsTasks = Seq(
      JsTask(1, "", "", JsActionType.FILLOUT, None, conditions = Seq[JsCondition]())
    ),
    sampleSolutions = Seq(
      WebSampleSolution(1, WebSolution(htmlSolution = "", jsSolution = "")),
    )
  )

  override def instantiateSolution(id: Int, exercise: WebExercise, part: WebExPart, solution: WebSolution, points: Points, maxPoints: Points): WebUserSolution =
    WebUserSolution(id, part, solution, points, maxPoints)

  // Views

  //  override def renderAdminExerciseEditForm(user: User, newEx: WebExercise, isCreation: Boolean, toolList: ToolList)
  //                                          (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
  //    views.html.idExercises.web.editWebExercise(user, WebExerciseForm.exerciseFormat.fill(newEx), isCreation, this, toolList)

  //  override def renderUserExerciseEditForm(user: User, newExForm: Form[WebExercise], isCreation: Boolean)
  //                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
  //    views.html.idExercises.web.editWebExerciseForm(user, newExForm, isCreation, this)

  override def renderExercisePreview(user: User, newExercise: WebExercise, saved: Boolean): Html =
    views.html.idExercises.web.webPreview(newExercise)

  override def renderExercise(user: User, collection: WebCollection, exercise: WebExercise, part: WebExPart, maybeOldSolution: Option[WebUserSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {
    val oldOrDefaultSolutionString: String = maybeOldSolution.map(oldSol =>
      part match {
        case WebExParts.HtmlPart => oldSol.solution.htmlSolution
        case WebExParts.JsPart   => oldSol.solution.jsSolution
      }).getOrElse(WebConsts.STANDARD_HTML)

    views.html.idExercises.web.webExercise(user, collection, exercise, part, oldOrDefaultSolutionString, this)
  }

  override def renderEditRest(exercise: WebExercise): Html =
    views.html.idExercises.web.editWebExRest(exercise)

  override def playground(user: User): Html =
    views.html.idExercises.web.webPlayground(user)

  // Correction

  private def onDriverGetError: Throwable => Try[WebCompleteResult] = {
    case syntaxError: WebDriverException =>
      Option(syntaxError.getCause) match {
        case Some(scriptException: ScriptException) => Failure(scriptException)
        case _                                      => ???
      }
    case otherError                      =>
      print(otherError)
      ???
  }

  private def onDriverGetSuccess(learnerSolution: String, exercise: WebExercise, part: WebExPart, driver: HtmlUnitDriver): Try[WebCompleteResult] = Try {
    part match {
      case WebExParts.HtmlPart =>
        val elementResults = exercise.htmlTasks.map(WebCorrector.evaluateHtmlTask(_, driver))
        WebCompleteResult(learnerSolution, exercise, part, elementResults, Seq[JsWebResult]())
      case WebExParts.JsPart   =>
        val jsWebResults = exercise.jsTasks.map(WebCorrector.evaluateJsTask(_, driver))
        WebCompleteResult(learnerSolution, exercise, part, Seq[ElementResult](), jsWebResults)
    }
  }

  override def correctEx(user: User, learnerSolution: WebSolution, collection: WebCollection, exercise: WebExercise, part: WebExPart): Try[WebCompleteResult] = {
    val toWrite = part match {
      case WebExParts.HtmlPart => learnerSolution.htmlSolution
      case WebExParts.JsPart   => learnerSolution.jsSolution
    }

    writeWebSolutionFile(user.username, exercise.id, part, toWrite) flatMap { _ =>

      val driver = new HtmlUnitDriver(true)
      Try {
        driver.get(getSolutionUrl(user, exercise.id, part))
      }.transform(_ => onDriverGetSuccess(toWrite, exercise, part, driver), onDriverGetError)
    }
  }

}
