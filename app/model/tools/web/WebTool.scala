package model.tools.web

import better.files.File
import better.files.File.OpenOptions
import initialData.InitialData
import initialData.web.WebInitialData
import model._
import model.points._
import model.tools._
import org.openqa.selenium.htmlunit.HtmlUnitDriver

import java.nio.file.StandardOpenOption
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object WebTool extends ToolWithParts("web", "Web") {

  override type SolInputType = FilesSolutionInput
  override type ExContType   = WebExerciseContent
  override type PartType     = WebExPart
  override type ResType      = WebResult

  type WebExercise = Exercise[WebExerciseContent]

  // noinspection TypeAnnotation
  override val jsonFormats = WebToolJsonProtocol

  // noinspection TypeAnnotation
  override val graphQlModels = WebGraphQLModels

  private val openOptions: OpenOptions = Seq(
    StandardOpenOption.TRUNCATE_EXISTING,
    StandardOpenOption.CREATE,
    StandardOpenOption.WRITE
  )

  def writeFilesSolutionFiles(targetDir: File, webSolution: IFilesSolution): Try[Seq[File]] = Try {
    webSolution.files.map { exerciseFile =>
      (targetDir / exerciseFile.name)
        .createFileIfNotExists(createParents = true)
        .write(exerciseFile.content)(openOptions)
    }
  }

  // Correction

  private def onDriverGetSuccess(exContent: WebExerciseContent, part: WebExPart, driver: HtmlUnitDriver): WebResult = part match {
    case WebExPart.HtmlPart =>
      val gradedHtmlTaskResults: Seq[GradedHtmlTaskResult] = exContent.siteSpec.htmlTasks
        .map(WebCorrector.evaluateHtmlTask(_, driver))
        .map(WebGrader.gradeHtmlTaskResult)

      WebResult(
        gradedHtmlTaskResults,
        Seq.empty,
        points = addUp(gradedHtmlTaskResults.map(_.elementSpecResult.points)),
        maxPoints = addUp(gradedHtmlTaskResults.map(_.elementSpecResult.maxPoints))
      )

    case WebExPart.JsPart =>
      val gradedJsTaskResults: Seq[GradedJsTaskResult] = exContent.siteSpec.jsTasks
        .map(WebCorrector.evaluateJsTask(_, driver))
        .map(WebGrader.gradeJsTaskResult)

      WebResult(
        Seq.empty,
        gradedJsTaskResults,
        points = addUp(gradedJsTaskResults.map(_.points)),
        maxPoints = addUp(gradedJsTaskResults.map(_.maxPoints))
      )
  }

  override def correctAbstract(
    user: User,
    solution: FilesSolutionInput,
    exercise: WebExercise,
    part: WebExPart
  )(implicit executionContext: ExecutionContext): Future[WebResult] = Future.fromTry {

    val fileName = exercise.content.siteSpec.fileName

    val solutionUrl = s"http://localhost:9080/${user.username}/${exercise.collectionId}/${exercise.exerciseId}/$fileName"

    for {
      _ <- writeFilesSolutionFiles(solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId), solution)

      driver = new HtmlUnitDriver(true)

      _ <- Try { driver.get(solutionUrl) }

    } yield onDriverGetSuccess(exercise.content, part, driver)
  }

  override val initialData: InitialData[WebExerciseContent] = WebInitialData.initialData

}
