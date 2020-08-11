package model.tools.programming

import better.files.File
import model.tools.programming.ProgrammingToolJsonProtocol.{
  simplifiedExecutionResultFileContentFormat,
  unitTestCorrectionResultsFileJsonReads
}
import play.api.libs.json.Json

import scala.util.{Failure, Success, Try}

object ResultsFileJsonFormat {

  def readSimplifiedExecutionResultFile(targetFile: File): Try[Seq[SimplifiedExecutionResult]] =
    for {
      fileContent <- Try(targetFile.contentAsString)
      jsValue     <- Try(Json.parse(fileContent))
      result <-
        simplifiedExecutionResultFileContentFormat
          .reads(jsValue)
          .fold(
            _ => Failure(new Exception("There has been an error reading a json programing result file!")),
            result => Success(result.results)
          )
    } yield result

  // Unit test correction

  def readTestCorrectionResultFile(targetFile: File): Try[Seq[UnitTestCorrectionResult]] =
    for {
      fileContent <- Try(targetFile.contentAsString)
      jsValue     <- Try(Json.parse(fileContent))
      result <-
        unitTestCorrectionResultsFileJsonReads
          .reads(jsValue)
          .fold(
            _ => Failure(new Exception("There has been an error reading a json programming result file!")),
            result => Success(result.results)
          )
    } yield result

}
