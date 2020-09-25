package model.tools.programming

import better.files.File
import model.core.{DockerBind, DockerConnector, ResultsFileJsonFormat}
import model.points._
import model.result.SuccessType
import model.tools.programming.ProgrammingToolJsonProtocol.simplifiedExecutionResultFileContentReads
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait ProgrammingSimpleImplementationCorrector extends ProgrammingAbstractCorrector {

  private def buildSimpleTestDataFileContent(completeTestData: Seq[ProgTestData]): JsValue =
    ProgrammingToolJsonProtocol.dumpCompleteTestDataToJson(completeTestData)

  def correctSimplifiedImplementation(
    defaultFileMounts: Seq[DockerBind],
    solTargetDir: File,
    simplifiedUnitTestPart: SimplifiedUnitTestPart,
    resultFile: File
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] = {

    val mp = maxPoints(simplifiedUnitTestPart)

    // write files
    val testMainFile = solTargetDir / testMainFileName
    testMainFile
      .createIfNotExists(createParents = true)
      .write(simplifiedUnitTestPart.simplifiedTestMainFile.content)

    val testDataFile = solTargetDir / testDataFileName
    testDataFile
      .createIfNotExists(createParents = true)
      .write(Json.prettyPrint(buildSimpleTestDataFileContent(simplifiedUnitTestPart.sampleTestData)))

    // mount files
    val otherDockerBinds = Seq(
      DockerBind(testDataFile, baseBindPath / testDataFileName, isReadOnly = true),
      DockerBind(testMainFile, baseBindPath / testMainFileName, isReadOnly = true)
    )

    DockerConnector
      .runContainer(
        imageName = programmingCorrectionDockerImage.name,
        maybeDockerBinds = defaultFileMounts ++ otherDockerBinds,
        maybeCmd = Some(Seq("simplified")),
        deleteContainerAfterRun = _ == 0
      )
      .map {
        case Failure(exception) =>
          onError("Error while running programming simplified execution docker image", mp, Some(exception))
        case Success(_) =>
          ResultsFileJsonFormat
            .readDockerExecutionResultFile(resultFile, simplifiedExecutionResultFileContentReads)
            .fold(
              exception => onError("Error while reading result file", mp, Some(exception)),
              simplifiedExecutionResultFileContent => {
                val results = simplifiedExecutionResultFileContent.results

                ProgrammingResult(
                  simplifiedResults = results,
                  points = results.count(ser => ser.success == SuccessType.COMPLETE).points,
                  maxPoints = mp
                )
              }
            )
      }
  }
}
