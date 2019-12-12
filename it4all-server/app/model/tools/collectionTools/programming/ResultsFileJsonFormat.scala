package model.tools.collectionTools.programming

import better.files.File
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}

import scala.util.{Failure, Success, Try}

object ResultsFileJsonFormat {

  private val logger = Logger(ResultsFileJsonFormat.getClass)

  def readSimplifiedExecutionResultFile(targetFile: File): Try[Seq[SimplifiedExecutionResult]] =
    Try(targetFile.contentAsString).flatMap { fileContent =>
      Try(Json.parse(fileContent)).map {
        jsValue =>

          val onError: scala.collection.Seq[Any] => Seq[SimplifiedExecutionResult] = errors => {
            logger.error("There has been an error reading a json programming result file: $error")
            errors.foreach(error => logger.error(error.toString))
            Seq.empty
          }

          Reads.seq(ProgrammingToolJsonProtocol.simplifiedExecutionResultFormat)
            .reads(jsValue)
            .fold(onError, identity)
      }
    }

  // Unit test correction

  def readTestCorrectionResultFile(targetFile: File): Try[Seq[UnitTestCorrectionResult]] =
    Try(Json.parse(targetFile.contentAsString)).flatMap { jsValue =>
      ProgrammingToolJsonProtocol.unitTestCorrectionResultsFileJsonReads.reads(jsValue) match {
        case JsSuccess(result, _) => Success(result.results)
        case JsError(errors)      =>
          errors.foreach(error => logger.error(s"There has been an error reading a json programming result file: $error"))
          Failure(new Exception("There has been an error reading a json programming result file!"))
      }
    }

}
