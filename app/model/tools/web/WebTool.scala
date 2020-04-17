package model.tools.web

import java.nio.file.StandardOpenOption

import better.files.File
import better.files.File.OpenOptions
import com.gargoylesoftware.htmlunit.ScriptException
import de.uniwue.webtester.WebCorrector
import de.uniwue.webtester.result._
import model.User
import model.persistence.DbExercise
import model.points.addUp
import model.tools._
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.libs.json.Reads

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object WebTool extends CollectionTool("web", "Web") {

  override type SolType        = WebSolution
  override type ExContentType  = WebExerciseContent
  override type ExerciseType   = WebExercise
  override type PartType       = WebExPart
  override type CompResultType = WebCompleteResult

  // Yaml, Html forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[WebSolution, WebExerciseContent, WebExercise, WebExPart] =
    WebToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[WebSolution, WebExerciseContent, WebExercise, WebExPart] =
    WebGraphQLModels

  // DB

  def writeWebSolutionFiles(
    username: String,
    collId: Int,
    exerciseId: Int,
    webSolution: WebSolution
  ): Try[Seq[File]] = Try {

    val targetDir: File = solutionDirForExercise(username, collId, exerciseId)

    val openOptions: OpenOptions =
      Seq(StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE)

    webSolution.files.map { exerciseFile =>
      val fileTargetPath = targetDir / exerciseFile.name

      fileTargetPath.createFileIfNotExists(createParents = true).write(exerciseFile.content)(openOptions)
    }
  }

  // Correction

  private def onDriverGetError: Throwable => Try[WebCompleteResult] = {
    case syntaxError: WebDriverException =>
      Option(syntaxError.getCause) match {
        case Some(scriptException: ScriptException) => Failure(scriptException)
        case _                                      => ???
      }
    case otherError =>
      print(otherError)
      ???
  }

  private def onDriverGetSuccess(
    exercise: WebExercise,
    part: WebExPart,
    driver: HtmlUnitDriver,
    solutionSaved: Boolean
  ): Try[WebCompleteResult] = Try {
    part match {
      case WebExParts.HtmlPart =>
        val htmlTaskResults: Seq[HtmlTaskResult] =
          exercise.content.siteSpec.htmlTasks.map(WebCorrector.evaluateHtmlTask(_, driver))
        val gradedHtmlTaskResults: Seq[GradedHtmlTaskResult] = htmlTaskResults.map(WebGrader.gradeHtmlTaskResult)

        val points    = addUp(gradedHtmlTaskResults.map(_.points))
        val maxPoints = addUp(gradedHtmlTaskResults.map(_.maxPoints))

        WebCompleteResult(gradedHtmlTaskResults, Seq[GradedJsTaskResult](), points, maxPoints, solutionSaved)

      case WebExParts.JsPart =>
        val jsTaskResults: Seq[JsTaskResult] =
          exercise.content.siteSpec.jsTasks.map(WebCorrector.evaluateJsTask(_, driver))
        val gradedJsTaskResults: Seq[GradedJsTaskResult] = jsTaskResults.map(WebGrader.gradeJsTaskResult)

        val points    = addUp(gradedJsTaskResults.map(_.points))
        val maxPoints = addUp(gradedJsTaskResults.map(_.maxPoints))

        WebCompleteResult(Seq[GradedHtmlTaskResult](), gradedJsTaskResults, points, maxPoints, solutionSaved)
    }
  }

  private def getSolutionUrl(user: User, exercise: WebExercise, fileName: String): String =
    s"http://localhost:9080/${user.username}/${exercise.collectionId}/${exercise.id}/$fileName"

  override def correctAbstract(
    user: User,
    learnerSolution: WebSolution,
    collection: ExerciseCollection,
    exercise: WebExercise,
    part: WebExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[WebCompleteResult]] = Future {
    writeWebSolutionFiles(user.username, collection.id, exercise.id, learnerSolution)
      .flatMap { _ =>
        val driver              = new HtmlUnitDriver(true)
        val solutionUrl: String = getSolutionUrl(user, exercise, exercise.content.siteSpec.fileName)

        Try(driver.get(solutionUrl))
          .fold(
            onDriverGetError,
            (_: Unit) => onDriverGetSuccess(exercise, part, driver, solutionSaved)
          )
      }
  }

  override protected def convertExerciseFromDb(dbExercise: DbExercise, topics: Seq[Topic]): Option[WebExercise] =
    dbExercise match {
      case DbExercise(id, collectionId, toolId, title, authors, text, difficulty, sampleSolutionsJson, contentJson) =>
        for {
          sampleSolutions <- Reads.seq(toolJsonProtocol.sampleSolutionFormat).reads(sampleSolutionsJson).asOpt
          content         <- toolJsonProtocol.exerciseContentFormat.reads(contentJson).asOpt
        } yield WebExercise(
          id,
          collectionId,
          toolId,
          title,
          authors,
          text,
          topics,
          difficulty,
          sampleSolutions,
          content
        )
    }

}
