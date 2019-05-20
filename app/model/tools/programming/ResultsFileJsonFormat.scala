package model.tools.programming

import better.files.File
import play.api.Logger
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

//noinspection ConvertibleToMethodValue
object ResultsFileJsonFormat {

  private val logger = Logger(ResultsFileJsonFormat.getClass)

  def readSimplifiedExecutionResultFile(targetFile: File): Try[Seq[SimplifiedExecutionResult]] =
    Try(Json.parse(targetFile.contentAsString)).flatMap { jsValue =>
      ProgJsonProtocols.simplifiedResultsFileJsonReads.reads(jsValue) match {
        case JsSuccess(result: SimplifiedResultFileContent, _) => Success(result.results)
        case JsError(errors)                                   =>
          errors.foreach(error => logger.error(s"There has been an error reading a json programming result file: $error"))
          Failure(new Exception("There has been an error reading a json programming result file!"))
      }
    }

  // Unit test correction

  def readTestCorrectionResultFile(targetFile: File): Try[Seq[UnitTestCorrectionResult]] =
    Try(Json.parse(targetFile.contentAsString)).flatMap { jsValue =>
      ProgJsonProtocols.unitTestCorrectionResultsFileJsonReads.reads(jsValue) match {
        case JsSuccess(result, _) => Success(result.results)
        case JsError(errors)      =>
          errors.foreach(error => logger.error(s"There has been an error reading a json programming result file: $error"))
          Failure(new Exception("There has been an error reading a json programming result file!"))
      }
    }

}
