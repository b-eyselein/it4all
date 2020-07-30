package model.tools.programming

import better.files.File
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
import model.points._
import model.result.SuccessType
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

    val maxPoints = simplifiedUnitTestPart.sampleTestData.size.points

    val testMainFile = solTargetDir / testMainFileName
    testMainFile
      .createIfNotExists(createParents = true)
      .write(simplifiedUnitTestPart.simplifiedTestMainFile.content)

    val testDataFile = solTargetDir / testDataFileName
    testDataFile
      .createIfNotExists(createParents = true)
      .write(Json.prettyPrint(buildSimpleTestDataFileContent(simplifiedUnitTestPart.sampleTestData)))

    val dockerBindPath = DockerConnector.DefaultWorkingDir

    val otherDockerBinds = Seq(
      DockerBind(testDataFile, s"$dockerBindPath/$testDataFileName", isReadOnly = true),
      DockerBind(testMainFile, s"$dockerBindPath/$testMainFileName", isReadOnly = true),
      DockerBind(resultFile, s"$dockerBindPath/$resultFileName")
    )

    DockerConnector
      .runContainer(
        imageName = programmingSimplifiedCorrectionDockerImageName.name,
        maybeDockerBinds = progSolutionFilesMounts ++ otherDockerBinds
      )
      .map {
        case Failure(exception) =>
          onError("Error while running programming simplified execution docker image", maxPoints, Some(exception))
        case Success(_) =>
          ResultsFileJsonFormat
            .readSimplifiedExecutionResultFile(resultFile)
            .fold(
              exception => onError("Error while reading result file", maxPoints, Some(exception)),
              simplifiedResults => {

                val points = simplifiedResults.count(ser => ser.success == SuccessType.COMPLETE).points

                ProgrammingResult(simplifiedResults = simplifiedResults, points = points, maxPoints = maxPoints)
              }
            )
      }
  }
}
