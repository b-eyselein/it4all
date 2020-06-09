package model.tools.programming

import better.files.File
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
import play.api.Logger
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ProgrammingSimpleImplementationCorrector extends ProgrammingAbstractCorrector {

  override protected val logger: Logger = Logger(ProgrammingSimpleImplementationCorrector.getClass)

  val programmingSimplifiedCorrectionDockerImageName: ScalaDockerImage =
    ScalaDockerImage("ls6uniwue", "py_simplified_prog_corrector", "0.3.1")

  def correctSimplifiedImplementation(
    solTargetDir: File,
    exerciseContent: ProgrammingExerciseContent,
    progSolutionFilesMounts: Seq[DockerBind],
    resultFile: File,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] =
    exerciseContent.unitTestPart.simplifiedTestMainFile.map(_.content) match {
      case None => Future.successful(onError("Die not find simplified execution main content", solutionSaved))
      case Some(simplifiedMainContent) =>
        val testMainFile = solTargetDir / testMainFileName
        createFileAndWrite(testMainFile, simplifiedMainContent)

        val testDataFile = solTargetDir / testDataFileName
        createFileAndWrite(
          testDataFile,
          Json.prettyPrint(exerciseContent.buildSimpleTestDataFileContent(exerciseContent.sampleTestData))
        )

        DockerConnector
          .runContainer(
            imageName = programmingSimplifiedCorrectionDockerImageName.name,
            maybeDockerBinds = progSolutionFilesMounts ++ Seq(
              DockerBind(testDataFile, s"${DockerConnector.DefaultWorkingDir}/$testDataFileName", isReadOnly = true),
              DockerBind(testMainFile, s"${DockerConnector.DefaultWorkingDir}/$testMainFileName", isReadOnly = true),
              DockerBind(solTargetDir / resultFileName, s"${DockerConnector.DefaultWorkingDir}/$resultFileName")
            )
          )
          .map {
            case Failure(exception) =>
              onError(
                "Error while running programming simplified execution docker image",
                solutionSaved,
                maybeException = Some(exception)
              )
            case Success(_) =>
              ResultsFileJsonFormat
                .readSimplifiedExecutionResultFile(resultFile)
                .fold(
                  exception =>
                    onError("Error while reading result file", solutionSaved, maybeException = Some(exception)),
                  results => ProgrammingResult(solutionSaved, simplifiedResults = results)
                )
          }
    }
}
