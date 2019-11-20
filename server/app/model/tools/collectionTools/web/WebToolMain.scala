package model.tools.collectionTools.web

import java.nio.file.StandardOpenOption

import better.files.File
import better.files.File.OpenOptions
import com.gargoylesoftware.htmlunit.ScriptException
import de.uniwue.webtester._
import javax.inject.{Inject, Singleton}
import model._
import model.points.{Points, addUp}
import model.tools.ToolJsonProtocol
import model.tools.collectionTools.{CollectionToolMain, ExerciseCollection, ExerciseFile, ExerciseFileJsonProtocol, LoadExerciseFilesMessage}
import model.tools.collectionTools.web.persistence.WebTableDefs
import net.jcazevedo.moultingyaml.YamlFormat
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.data._
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

@Singleton
class WebToolMain @Inject()(val tables: WebTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain(WebConsts) {

  override type PartType = WebExPart
  override type ExType = WebExercise


  override type SolType = Seq[ExerciseFile]
  override type SampleSolType = FilesSampleSolution
  override type UserSolType = FilesUserSolution[WebExPart]

  override type ReviewType = WebExerciseReview

  override type ResultType = GradedWebTaskResult
  override type CompResultType = WebCompleteResult

  override type Tables = WebTableDefs

  // Other members

  override val hasPlayground: Boolean        = true
  override val exParts      : Seq[WebExPart] = WebExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: ToolJsonProtocol[WebExercise, FilesSampleSolution, WebCompleteResult] =
    WebToolJsonProtocol

  override protected val exerciseYamlFormat: YamlFormat[WebExercise] = WebToolYamlProtocol.webExerciseYamlFormat

  override val exerciseReviewForm: Form[WebExerciseReview] = WebToolForms.exerciseReviewForm

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

  override def instantiateSolution(id: Int, exercise: WebExercise, part: WebExPart, solution: Seq[ExerciseFile], points: Points, maxPoints: Points): FilesUserSolution[WebExPart] =
    FilesUserSolution[WebExPart](id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: WebCompleteResult, solSaved: Boolean): WebCompleteResult =
    compResult.copy(solutionSaved = solSaved)

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
        val htmlTaskResults      : Seq[HtmlTaskResult]       = exercise.siteSpec.htmlTasks.map(WebCorrector.evaluateHtmlTask(_, driver))
        val gradedHtmlTaskResults: Seq[GradedHtmlTaskResult] = htmlTaskResults.map(WebGrader.gradeHtmlTaskResult)

        val points    = addUp(gradedHtmlTaskResults.map(_.points))
        val maxPoints = addUp(gradedHtmlTaskResults.map(_.maxPoints))

        WebCompleteResult(gradedHtmlTaskResults, Seq[GradedJsTaskResult](), points, maxPoints)

      case WebExParts.JsPart =>
        val jsTaskResults      : Seq[JsTaskResult]       = exercise.siteSpec.jsTasks.map(WebCorrector.evaluateJsTask(_, driver))
        val gradedJsTaskResults: Seq[GradedJsTaskResult] = jsTaskResults.map(WebGrader.gradeJsTaskResult)

        val points    = addUp(gradedJsTaskResults.map(_.points))
        val maxPoints = addUp(gradedJsTaskResults.map(_.maxPoints))

        WebCompleteResult(Seq[GradedHtmlTaskResult](), gradedJsTaskResults, points, maxPoints)
    }
  }

  def getSolutionUrl(user: User, collId: Int, exerciseId: Int, fileName: String): String =
    s"http://localhost:9080/${user.username}/${collId}/${exerciseId}/${fileName}"

  override def correctEx(user: User, learnerSolution: Seq[ExerciseFile], collection: ExerciseCollection, exercise: WebExercise, part: WebExPart): Future[Try[WebCompleteResult]] = Future {
    writeWebSolutionFiles(user.username, collection.id, exercise.id, part, learnerSolution).flatMap { _ =>

      val driver              = new HtmlUnitDriver(true)
      val solutionUrl: String = getSolutionUrl(user, collection.id, exercise.id, exercise.siteSpec.fileName)

      Try {
        driver.get(solutionUrl)
      }.fold(onDriverGetError, _ => onDriverGetSuccess(learnerSolution, exercise, part, driver))
    }
  }

  override def futureFilesForExercise(user: User, collId: Int, exercise: WebExercise, part: WebExPart): Future[LoadExerciseFilesMessage] =
    futureMaybeOldSolution(user.username, collId, exercise.id, part).map {
      case None           => exercise.filesForExercisePart(part)
      case Some(solution) => LoadExerciseFilesMessage(solution.solution, None)
    }

}
