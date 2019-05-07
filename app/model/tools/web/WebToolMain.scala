package model.tools.web

import java.nio.file.StandardOpenOption

import better.files.File
import better.files.File.OpenOptions
import com.gargoylesoftware.htmlunit.ScriptException
import de.uniwue.webtester._
import javax.inject.{Inject, Singleton}
import model._
import model.core.result.CompleteResultJsonProtocol
import model.points.{Points, addUp}
import model.toolMains._
import model.tools.web.persistence.WebTableDefs
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.Logger
import play.api.data._
import play.api.i18n.MessagesProvider
import play.api.libs.json.{Format, JsError, JsSuccess}
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

@Singleton
class WebToolMain @Inject()(val tables: WebTableDefs)(implicit ec: ExecutionContext) extends CollectionToolMain("Web", "web") {

  // Result types

  override type PartType = WebExPart
  override type ExType = WebExercise
  override type CollType = WebCollection


  override type SolType = Seq[ExerciseFile]
  override type SampleSolType = FilesSampleSolution
  override type UserSolType = FilesUserSolution[WebExPart]

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

  override val sampleSolutionJsonFormat: Format[FilesSampleSolution] = FilesSampleSolutionJsonProtocol.filesSampleSolutionFormat

  override protected val completeResultJsonProtocol: CompleteResultJsonProtocol[GradedWebTaskResult, WebCompleteResult] = WebCompleteResultJsonProtocol

  // DB

  def writeWebSolutionFiles(username: String, collId: Int, exerciseId: Int, part: WebExPart, exerciseFiles: Seq[ExerciseFile]): Try[Seq[File]] = Try {

    val targetDir: File = solutionDirForExercise(username, collId, exerciseId)

    val openOptions: OpenOptions = Seq(StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE)

    exerciseFiles.map { exerciseFile: ExerciseFile =>
      val fileTargetPath = targetDir / exerciseFile.name

      fileTargetPath.createFileIfNotExists(createParents = true).write(exerciseFile.content)(openOptions)
    }
  }

  override def futureMaybeOldSolution(username: String, collId: Int, exId: Int, part: WebExPart): Future[Option[UserSolType]] = part match {
    case WebExParts.HtmlPart => super.futureMaybeOldSolution(username, collId, exId, part)
    case WebExParts.JsPart   =>
      super.futureMaybeOldSolution(username, collId, exId, WebExParts.JsPart) flatMap {
        case Some(solution) => Future.successful(Some(solution))
        case None           =>
          super.futureMaybeOldSolution(username, collId, exId, WebExParts.HtmlPart)
      }
  }

  override def futureUserCanSolveExPart(username: String, collId: Int, exId: Int, part: WebExPart): Future[Boolean] = part match {
    case WebExParts.HtmlPart => Future.successful(true)
    case WebExParts.JsPart   => futureMaybeOldSolution(username, collId, exId, WebExParts.HtmlPart).map(_.exists(r => r.points == r.maxPoints))
  }

  // Other helper methods

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
      //      WebSampleSolution(1, WebSolution(htmlSolution = "", jsSolution = Some(""))),
    )
  )

  override def instantiateSolution(id: Int, exercise: WebExercise, part: WebExPart, solution: Seq[ExerciseFile], points: Points, maxPoints: Points): FilesUserSolution[WebExPart] =
    FilesUserSolution[WebExPart](id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: WebCompleteResult, solSaved: Boolean): WebCompleteResult =
    compResult.copy(solutionSaved = solSaved)

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

  override def renderExercise(user: User, collection: WebCollection, exercise: WebExercise, part: WebExPart, maybeOldSolution: Option[FilesUserSolution[WebExPart]])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {
    //    val oldOrDefaultSolutionString: String = maybeOldSolution.map(oldSol =>
    //      part match {
    //        case WebExParts.HtmlPart => oldSol.solution.htmlSolution
    //        case WebExParts.JsPart   => oldSol.solution.jsSolution.getOrElse(oldSol.solution.htmlSolution)
    //      }).getOrElse(WebConsts.STANDARD_HTML)

    views.html.toolViews.web.webExercise(user, collection, exercise, part, /*oldOrDefaultSolutionString,*/ this)
  }

  override def renderEditRest(exercise: WebExercise): Html =
    views.html.toolViews.web.editWebExRest(exercise)

  override def playground(user: User): Html =
    views.html.toolViews.web.webPlayground(user)

  // Correction

  override protected def readSolution(request: Request[AnyContent], part: WebExPart): Either[String, Seq[ExerciseFile]] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) =>
      ExerciseFileJsonProtocol.exerciseFileWorkspaceReads.reads(jsValue) match {
        case JsSuccess(x, _) => Right(x.files)
        case JsError(errors) => Left(errors.toString())
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

  private def onDriverGetSuccess(learnerSolution: Seq[ExerciseFile], exercise: WebExercise, part: WebExPart, driver: HtmlUnitDriver): Try[WebCompleteResult] = Try {
    part match {
      case WebExParts.HtmlPart =>
        val htmlTaskResults: Seq[HtmlTaskResult] = exercise.siteSpec.htmlTasks.map(WebCorrector.evaluateHtmlTask(_, driver))
        val gradedHtmlTaskResults: Seq[GradedHtmlTaskResult] = htmlTaskResults.map(WebGrader.gradeHtmlTaskResult)

        val points = addUp(gradedHtmlTaskResults.map(_.points))
        val maxPoints = addUp(gradedHtmlTaskResults.map(_.maxPoints))

        WebCompleteResult(gradedHtmlTaskResults, Seq[GradedJsTaskResult](), points, maxPoints)

      case WebExParts.JsPart =>
        val jsTaskResults: Seq[JsTaskResult] = exercise.siteSpec.jsTasks.map(WebCorrector.evaluateJsTask(_, driver))
        val gradedJsTaskResults: Seq[GradedJsTaskResult] = jsTaskResults.map(WebGrader.gradeJsTaskResult)

        val points = addUp(gradedJsTaskResults.map(_.points))
        val maxPoints = addUp(gradedJsTaskResults.map(_.maxPoints))

        WebCompleteResult(Seq[GradedHtmlTaskResult](), gradedJsTaskResults, points, maxPoints)
    }
  }

  def getSolutionUrl(user: User, collId: Int, exerciseId: Int, fileName: String): String =
    s"http://localhost:9080/${user.username}/${collId}/${exerciseId}/${fileName}"

  override def correctEx(user: User, learnerSolution: Seq[ExerciseFile], collection: WebCollection, exercise: WebExercise, part: WebExPart): Future[Try[WebCompleteResult]] = Future {
    writeWebSolutionFiles(user.username, collection.id, exercise.id, part, learnerSolution).flatMap { _ =>

      val driver = new HtmlUnitDriver(true)
      val solutionUrl: String = getSolutionUrl(user, collection.id, exercise.id, exercise.siteSpec.fileName)

      Try {
        driver.get(solutionUrl)
      }.fold(onDriverGetError, _ => onDriverGetSuccess(learnerSolution, exercise, part, driver))
    }
  }

  override def futureFilesForExercise(user: User, collId: Int, exercise: WebExercise, part: WebExPart): Future[Seq[ExerciseFile]] =
    futureMaybeOldSolution(user.username, collId, exercise.id, part).map {
      case None           => exercise.files
      case Some(solution) => solution.solution
    }

}
