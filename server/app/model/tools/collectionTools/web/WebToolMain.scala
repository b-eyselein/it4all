package model.tools.collectionTools.web

import java.nio.file.StandardOpenOption

import better.files.File
import better.files.File.OpenOptions
import com.gargoylesoftware.htmlunit.ScriptException
import de.uniwue.webtester._
import model.User
import model.points.addUp
import model.tools.collectionTools._
import net.jcazevedo.moultingyaml.YamlFormat
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.htmlunit.HtmlUnitDriver

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object WebToolMain extends CollectionToolMain(WebConsts) {

  override type PartType = WebExPart
  override type ExContentType = WebExerciseContent
  override type SolType = Seq[ExerciseFile]
  override type CompResultType = WebCompleteResult

  // Other members

  override val hasPlayground: Boolean        = true
  override val exParts      : Seq[WebExPart] = WebExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: FilesSampleSolutionToolJsonProtocol[WebExerciseContent, WebCompleteResult] =
    WebToolJsonProtocol

  override protected val exerciseContentYamlFormat: YamlFormat[WebExerciseContent] = WebToolYamlProtocol.webExerciseYamlFormat

  // DB

  def writeWebSolutionFiles(username: String, collId: Int, exerciseId: Int, part: WebExPart, exerciseFiles: Seq[ExerciseFile]): Try[Seq[File]] = Try {

    val targetDir: File = solutionDirForExercise(username, collId, exerciseId)

    val openOptions: OpenOptions = Seq(StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE)

    exerciseFiles.map { exerciseFile =>
      val fileTargetPath = targetDir / exerciseFile.name

      fileTargetPath.createFileIfNotExists(createParents = true).write(exerciseFile.content)(openOptions)
    }
  }

  //  override def futureMaybeOldSolution(username: String, collId: Int, exId: Int, part: WebExPart): Future[Option[UserSolType]] = part match {
  //    case WebExParts.HtmlPart => super.futureMaybeOldSolution(username, collId, exId, part)
  //    case WebExParts.JsPart   =>
  //      super.futureMaybeOldSolution(username, collId, exId, WebExParts.JsPart) flatMap {
  //        case Some(solution) => Future.successful(Some(solution))
  //        case None           =>
  //          super.futureMaybeOldSolution(username, collId, exId, WebExParts.HtmlPart)
  //      }
  //  }

  // Other helper methods

  override protected def exerciseHasPart(exercise: WebExerciseContent, partType: WebExPart): Boolean = partType match {
    case WebExParts.HtmlPart => exercise.siteSpec.htmlTasks.nonEmpty
    case WebExParts.JsPart   => exercise.siteSpec.jsTasks.nonEmpty
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

  private def onDriverGetSuccess(
    exercise: WebExerciseContent,
    part: WebExPart,
    driver: HtmlUnitDriver,
    solutionSaved: Boolean
  ): Try[WebCompleteResult] = Try {
    part match {
      case WebExParts.HtmlPart =>
        val htmlTaskResults      : Seq[HtmlTaskResult]       = exercise.siteSpec.htmlTasks.map(WebCorrector.evaluateHtmlTask(_, driver))
        val gradedHtmlTaskResults: Seq[GradedHtmlTaskResult] = htmlTaskResults.map(WebGrader.gradeHtmlTaskResult)

        val points    = addUp(gradedHtmlTaskResults.map(_.points))
        val maxPoints = addUp(gradedHtmlTaskResults.map(_.maxPoints))

        WebCompleteResult(gradedHtmlTaskResults, Seq[GradedJsTaskResult](), points, maxPoints, solutionSaved)

      case WebExParts.JsPart =>
        val jsTaskResults      : Seq[JsTaskResult]       = exercise.siteSpec.jsTasks.map(WebCorrector.evaluateJsTask(_, driver))
        val gradedJsTaskResults: Seq[GradedJsTaskResult] = jsTaskResults.map(WebGrader.gradeJsTaskResult)

        val points    = addUp(gradedJsTaskResults.map(_.points))
        val maxPoints = addUp(gradedJsTaskResults.map(_.maxPoints))

        WebCompleteResult(Seq[GradedHtmlTaskResult](), gradedJsTaskResults, points, maxPoints, solutionSaved)
    }
  }

  def getSolutionUrl(user: User, exercise: Exercise, fileName: String): String =
    s"http://localhost:9080/${user.username}/${String.valueOf(exercise.collectionId)}/${String.valueOf(exercise.id)}/${fileName}"

  override def correctEx(
    user: User,
    learnerSolution: Seq[ExerciseFile],
    collection: ExerciseCollection,
    exercise: Exercise,
    content: WebExerciseContent,
    part: WebExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[WebCompleteResult]] = Future {
    writeWebSolutionFiles(user.username, collection.id, exercise.id, part, learnerSolution)
      .flatMap { _ =>

        val driver              = new HtmlUnitDriver(true)
        val solutionUrl: String = getSolutionUrl(user, exercise, content.siteSpec.fileName)

        Try(driver.get(solutionUrl))
          .fold(
            onDriverGetError,
            (_: Unit) => onDriverGetSuccess(content, part, driver, solutionSaved)
          )
      }
  }

}
