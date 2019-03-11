package model.tools.web

import better.files.File
import com.gargoylesoftware.htmlunit.ScriptException
import de.uniwue.webtester._
import javax.inject.{Inject, Singleton}
import model.core.result.CompleteResultJsonProtocol
import model.points.Points
import model.toolMains._
import model.tools.web.persistence.WebTableDefs
import model._
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.Logger
import play.api.data._
import play.api.i18n.MessagesProvider
import play.api.libs.json.{JsString, Json}
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

  override type ReviewType = WebExerciseReview

  override type ResultType = GradedWebTaskResult
  override type CompResultType = WebCompleteResult

  override type Tables = WebTableDefs


  // Other members

  override val hasPlayground: Boolean = true

  override val toolState: ToolState = ToolState.LIVE

  override val exParts: Seq[WebExPart] = WebExParts.values

  // Yaml, Html forms, Json

  override protected val collectionYamlFormat: MyYamlFormat[WebCollection] = WebToolYamlProtocol.WebCollectionYamlFormat
  override protected val exerciseYamlFormat  : MyYamlFormat[WebExercise]   = WebToolYamlProtocol.WebExYamlFormat

  override val collectionForm    : Form[WebCollection]     = WebToolForms.collectionFormat
  override val exerciseForm      : Form[WebExercise]       = WebToolForms.exerciseFormat
  override val exerciseReviewForm: Form[WebExerciseReview] = WebToolForms.exerciseReviewForm

  override protected val completeResultJsonProtocol: CompleteResultJsonProtocol[GradedWebTaskResult, WebCompleteResult] = WebCompleteResultJsonProtocol

  // DB

  def writeWebSolutionFile(username: String, exerciseId: Int, part: WebExPart, content: String): Try[File] = Try {
    val target: File = solutionDirForExercise(username, exerciseId) / s"test.html"
    target.createFileIfNotExists(createParents = true).write(content)
  }

  override def futureMaybeOldSolution(username: String, collId: Int, exId: Int, part: WebExPart): Future[Option[UserSolType]] = part match {
    case WebExParts.HtmlPart => super.futureMaybeOldSolution(username, collId, exId, part)
    case WebExParts.JsPart   =>
      super.futureMaybeOldSolution(username, collId, exId, WebExParts.JsPart) flatMap {
        case Some(solution) => Future.successful(Some(solution))
        case None           => super.futureMaybeOldSolution(username, collId, exId, WebExParts.HtmlPart)
      }
  }

  override def futureUserCanSolveExPart(username: String, collId: Int, exId: Int, part: WebExPart): Future[Boolean] = part match {
    case WebExParts.HtmlPart => Future.successful(true)
    case WebExParts.JsPart   => futureMaybeOldSolution(username, collId, exId, WebExParts.HtmlPart).map(_.exists(r => r.points == r.maxPoints))
  }

  // Other helper methods

  def getSolutionUrl(user: User, exerciseId: Int, part: WebExPart): String =
    s"http://localhost:9080/${user.username}/$exerciseId/test.html"

  override protected def exerciseHasPart(exercise: WebExercise, partType: WebExPart): Boolean = partType match {
    case WebExParts.HtmlPart => exercise.siteSpec.htmlTasks.nonEmpty
    case WebExParts.JsPart   => exercise.siteSpec.jsTasks.nonEmpty
  }

  override def instantiateCollection(id: Int, author: String, state: ExerciseState): WebCollection =
    WebCollection(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): WebExercise = WebExercise(
    id, SemanticVersionHelper.DEFAULT, title = "", author, text = "", state, htmlText = None, jsText = None,
    SiteSpec(
      1, "",
      htmlTasks = Seq(HtmlTask("", HtmlElementSpec(1, "", "", None, attributes = Seq[HtmlAttribute]()))),
      jsTasks = Seq(
        JsTask(1, "", preConditions = Seq[HtmlElementSpec](), JsAction("", JsActionType.FillOut, None), postConditions = Seq[HtmlElementSpec]())
      )
    ),
    files = Seq.empty,
    sampleSolutions = Seq(
      WebSampleSolution(1, WebSolution(htmlSolution = "", jsSolution = Some(""))),
    )
  )

  override def instantiateSolution(id: Int, exercise: WebExercise, part: WebExPart, solution: WebSolution, points: Points, maxPoints: Points): WebUserSolution =
    WebUserSolution(id, part, solution, points, maxPoints)

  // Views

  override def previewExerciseRest(ex: Exercise): Html = ex match {
    case we: WebExercise => views.html.toolViews.web.previewWebExerciseRest(we)
    case _               => ???
  }

  //  override def renderAdminExerciseEditForm(user: User, newEx: WebExercise, isCreation: Boolean, toolList: ToolList)
  //                                          (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
  //    views.html.idExercises.web.adminEditWebExercise(user, WebExerciseForm.exerciseFormat.fill(newEx), isCreation, this, toolList)

  //  override def renderUserExerciseEditForm(user: User, newExForm: Form[WebExercise], isCreation: Boolean)
  //                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
  //    views.html.idExercises.web.editWebExerciseForm(user, newExForm, isCreation, this)

  override def renderExercisePreview(user: User, collId: Int, newExercise: WebExercise, saved: Boolean): Html =
    views.html.toolViews.web.webPreview(newExercise)

  override def renderExercise(user: User, collection: WebCollection, exercise: WebExercise, part: WebExPart, maybeOldSolution: Option[WebUserSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {
    val oldOrDefaultSolutionString: String = maybeOldSolution.map(oldSol =>
      part match {
        case WebExParts.HtmlPart => oldSol.solution.htmlSolution
        case WebExParts.JsPart   => oldSol.solution.jsSolution.getOrElse(oldSol.solution.htmlSolution)
      }).getOrElse(WebConsts.STANDARD_HTML)

    views.html.toolViews.web.webExercise(user, collection, exercise, part, oldOrDefaultSolutionString, this)
  }

  override def renderEditRest(exercise: WebExercise): Html =
    views.html.toolViews.web.editWebExRest(exercise)

  override def playground(user: User): Html =
    views.html.toolViews.web.webPlayground(user)

  // Correction

  override protected def readSolution(user: User, collection: WebCollection, exercise: WebExercise, part: WebExPart)
                                     (implicit request: Request[AnyContent]): Option[WebSolution] = {
    println(request.body.asJson.map(Json.prettyPrint))

    request.body.asJson flatMap {
      case JsString(solution) =>
        part match {
          case WebExParts.HtmlPart => Some(WebSolution(htmlSolution = solution, jsSolution = None))
          case WebExParts.JsPart   => Some(WebSolution(htmlSolution = "", jsSolution = Some(solution)))
        }
      case other              =>
        logger.error("Wrong json content: " + other.toString)
        None
    }
  }

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
    val siteSpec = exercise.siteSpec
    part match {
      case WebExParts.HtmlPart =>
        val elementResults = siteSpec.htmlTasks.map(WebCorrector.evaluateHtmlTask(_, driver)).map(WebGrader.gradeHtmlTaskResult)
        WebCompleteResult(learnerSolution, exercise, part, elementResults, Seq[GradedJsTaskResult]())
      case WebExParts.JsPart   =>
        val jsWebResults = siteSpec.jsTasks.map(WebCorrector.evaluateJsTask(_, driver)).map(WebGrader.gradeJsTaskResult)
        WebCompleteResult(learnerSolution, exercise, part, Seq[GradedHtmlTaskResult](), jsWebResults)
    }
  }

  override def correctEx(user: User, learnerSolution: WebSolution, collection: WebCollection, exercise: WebExercise, part: WebExPart): Future[Try[WebCompleteResult]] = Future {
    val toWrite = part match {
      case WebExParts.HtmlPart => learnerSolution.htmlSolution
      case WebExParts.JsPart   => learnerSolution.jsSolution.getOrElse("")
    }

    writeWebSolutionFile(user.username, exercise.id, part, toWrite) flatMap { _ =>

      val driver = new HtmlUnitDriver(true)
      Try {
        driver.get(getSolutionUrl(user, exercise.id, part))
      }.transform(_ => onDriverGetSuccess(toWrite, exercise, part, driver), onDriverGetError)
    }
  }

  override def filesForExercise(collId: Int, ex: WebExercise): Seq[ExerciseFile] = ex.files

}
