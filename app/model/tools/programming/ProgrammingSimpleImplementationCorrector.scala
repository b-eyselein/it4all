package model.tools.programming

import better.files.File
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ProgrammingSimpleImplementationCorrector extends ProgrammingAbstractCorrector {

  override protected val logger: Logger = Logger(ProgrammingSimpleImplementationCorrector.getClass)

  val programmingSimplifiedCorrectionDockerImageName: ScalaDockerImage =
    ScalaDockerImage("ls6uniwue", "py_simplified_prog_corrector", "0.3.1")

  private def buildSimpleTestDataFileContent(completeTestData: Seq[ProgTestData]): JsValue =
    ProgrammingToolJsonProtocol.dumpCompleteTestDataToJson(completeTestData)

  def correctSimplifiedImplementation(
    solTargetDir: File,
    simplifiedUnitTestPart: SimplifiedUnitTestPart,
    progSolutionFilesMounts: Seq[DockerBind],
    resultFile: File
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] = {

    val testMainFile = solTargetDir / testMainFileName
    createFileAndWrite(testMainFile, simplifiedUnitTestPart.simplifiedTestMainFile.content)

    val testDataFile = solTargetDir / testDataFileName
    createFileAndWrite(
      testDataFile,
      Json.prettyPrint(buildSimpleTestDataFileContent(simplifiedUnitTestPart.sampleTestData))
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
            maybeException = Some(exception)
          )
        case Success(_) =>
          ResultsFileJsonFormat
            .readSimplifiedExecutionResultFile(resultFile)
            .fold(
              exception => onError("Error while reading result file", maybeException = Some(exception)),
              results => ProgrammingResult(simplifiedResults = results)
            )
      }
    /*
    }
     */
  }
}
