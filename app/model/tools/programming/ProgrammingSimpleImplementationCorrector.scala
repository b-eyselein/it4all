package model.tools.programming

import better.files.File
import model.core.DockerBind
import model.points._
import model.result.SuccessType
import model.tools.programming.ProgrammingToolJsonProtocol.simplifiedExecutionResultReads
import play.api.libs.json.{JsValue, Json, Reads}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait ProgrammingSimpleImplementationCorrector extends ProgrammingAbstractCorrector {

  private def buildSimpleTestDataFileContent(completeTestData: Seq[ProgTestData]): JsValue =
    ProgrammingToolJsonProtocol.dumpCompleteTestDataToJson(completeTestData)

  def correctSimplifiedImplementation(
    defaultFileMounts: Seq[DockerBind],
    solTargetDir: File,
    simplifiedUnitTestPart: SimplifiedUnitTestPart,
    resultFile: File
  )(implicit ec: ExecutionContext): Future[Try[ProgrammingResult]] = {

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

    runContainer(
      defaultFileMounts ++ otherDockerBinds,
      Reads.seq(simplifiedExecutionResultReads),
      resultFile,
      maybeCmd = Some(Seq("simplified")),
      deleteContainerAfterRun = _ == 0
    )(results =>
      ProgrammingResult(
        simplifiedResults = results,
        points = results.count(ser => ser.success == SuccessType.COMPLETE).points,
        maxPoints = maxPoints(simplifiedUnitTestPart)
      )
    )
  }
}
