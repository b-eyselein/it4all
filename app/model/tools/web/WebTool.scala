package model.tools.web

import java.nio.file.StandardOpenOption

import better.files.File
import better.files.File.OpenOptions
import de.uniwue.webtester.WebCorrector
import model.User
import model.points._
import model.tools._
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

object WebTool extends CollectionTool("web", "Web") {

  private val logger = Logger(WebTool.getClass)

  override type SolType        = WebSolution
  override type ExContentType  = WebExerciseContent
  override type PartType       = WebExPart
  override type CompResultType = WebAbstractResult

  // Yaml, Html forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[WebSolution, WebExerciseContent, WebExPart] =
    WebToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[WebSolution, WebExerciseContent, WebExPart] =
    WebGraphQLModels

  // DB

  private val openOptions: OpenOptions = Seq(
    StandardOpenOption.TRUNCATE_EXISTING,
    StandardOpenOption.CREATE,
    StandardOpenOption.WRITE
  )

  def writeWebSolutionFiles(targetDir: File, webSolution: WebSolution): Try[Seq[File]] = Try {
    webSolution.files.map { exerciseFile =>
      (targetDir / exerciseFile.name)
        .createFileIfNotExists(createParents = true)
        .write(exerciseFile.content)(openOptions)
    }
  }

  // Correction

  private def onDriverGetSuccess(
    exContent: WebExerciseContent,
    part: WebExPart,
    driver: HtmlUnitDriver,
    solutionSaved: Boolean
  ): Unit => WebCompleteResult = _ => {
    part match {
      case WebExPart.HtmlPart =>
        val gradedHtmlTaskResults: Seq[GradedHtmlTaskResult] = exContent.siteSpec.htmlTasks
          .map(WebCorrector.evaluateHtmlTask(_, driver))
          .map(WebGrader.gradeHtmlTaskResult)

        val points    = addUp(gradedHtmlTaskResults.map(_.points))
        val maxPoints = addUp(gradedHtmlTaskResults.map(_.maxPoints))

        WebCompleteResult(gradedHtmlTaskResults, Seq.empty, points, maxPoints, solutionSaved)

      case WebExPart.JsPart =>
        val gradedJsTaskResults: Seq[GradedJsTaskResult] = exContent.siteSpec.jsTasks
          .map(WebCorrector.evaluateJsTask(_, driver))
          .map(WebGrader.gradeJsTaskResult)

        val points    = addUp(gradedJsTaskResults.map(_.points))
        val maxPoints = addUp(gradedJsTaskResults.map(_.maxPoints))

        WebCompleteResult(Seq.empty, gradedJsTaskResults, points, maxPoints, solutionSaved)
    }
  }

  def onCorrectionError(
    msg: String,
    solutionSaved: Boolean,
    maxPoints: Points = (-1).points
  ): Throwable => WebAbstractResult = error => {
    logger.error(msg, error)

    WebInternalErrorResult(solutionSaved, maxPoints)
  }

  override def correctAbstract(
    user: User,
    solution: WebSolution,
    exercise: Exercise[WebSolution, WebExerciseContent],
    part: WebExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[WebAbstractResult]] = Future {
    Success {
      writeWebSolutionFiles(solutionDirForExercise(user.username, exercise.collectionId, exercise.id), solution)
        .fold(
          onCorrectionError("Error while writing user solution", solutionSaved),
          _ => {
            val driver = new HtmlUnitDriver(true)

            val fileName = exercise.content.siteSpec.fileName
            val solutionUrl =
              s"http://localhost:9080/${user.username}/${exercise.collectionId}/${exercise.id}/$fileName"

            Try(driver.get(solutionUrl)).fold(
              onCorrectionError(s"Error while looking up url $solutionUrl", solutionSaved),
              onDriverGetSuccess(exercise.content, part, driver, solutionSaved)
            )
          }
        )
    }
  }

}
