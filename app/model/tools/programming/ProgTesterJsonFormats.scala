package model.tools.programming

import better.files.File
import model.core.result.SuccessType
import model.tools.programming.ProgConsts._
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

object TestDataJsonFormat {

  private implicit val progUserTestDataWrites: Writes[ProgUserTestData] = Json.writes[ProgUserTestData]

  private implicit val progSampleTestDataWrites: Writes[ProgSampleTestData] = Json.writes[ProgSampleTestData]

  private val progTestDataWrites: Writes[ProgTestData] = Json.writes[ProgTestData]

  def dumpCompleteTestDataToJson(exercise: ProgExercise, testData: Seq[ProgTestData]): JsValue = Json.obj(
    testDataName -> JsArray(testData.map(progTestDataWrites.writes)),
    baseDataName -> exercise.baseData
  )

}

final case class UnitTestTestData(foldername: String, filename: String, testConfigs: Seq[UnitTestTestConfig])

//noinspection ConvertibleToMethodValue
object ResultsFileJsonFormat {

  private val logger = Logger(ResultsFileJsonFormat.getClass)

  // TODO: Json.reads[ExecutionResult]
  private implicit val executionResultJsonReads: Reads[ExecutionResult] = Json.reads[ExecutionResult]
  //    (
  //    (__ \ "success").read[SuccessType] and
  //      (__ \ "test_id").read[Int] and
  //      (__ \ "test_input").read[JsValue] and
  //      (__ \ "awaited").read[JsValue] and
  //      (__ \ "gotten").read[JsValue] and
  //      (__ \ "stdout").readNullable[String]
  //    ) (ExecutionResult.apply(_, _, _, _, _, _))

  // TODO: Json.reads[ResultFileContent]
  private val resultsFileJsonReads: Reads[ResultFileContent] = Json.reads[ResultFileContent]
  //    (
  //    (__ \ "result_type").read[String] and
  //      (__ \ "results").read[Seq[ExecutionResult]] and
  //      (__ \ "errors").read[String]
  //    ) (ResultFileContent.apply(_, _, _))

  def readImplCorrectionResultFile(targetFile: File): Try[Seq[ExecutionResult]] =
    Try(Json.parse(targetFile.contentAsString)).flatMap { jsValue =>
      resultsFileJsonReads.reads(jsValue) match {
        case JsSuccess(result: ResultFileContent, _) => Success(result.results)
        case JsError(errors)                         =>
          errors.foreach(error => logger.error(s"There has been an error reading a json programming result file: $error"))
          Failure(new Exception("There has been an error reading a json programming result file!"))
      }
    }

  // Unit test correction

  private implicit val unitTestTestConfigFormat: Format[UnitTestTestConfig] = Json.format[UnitTestTestConfig]

  private implicit val unitTestCorrectionResultReads: Reads[UnitTestCorrectionResult] = Json.reads[UnitTestCorrectionResult]

  private val unitTestCorrectionResultsFileJsonReads: Reads[UnitTestCorrectionResultFileContent] = Json.reads[UnitTestCorrectionResultFileContent]

  val unitTestDataWrites: Writes[UnitTestTestData] = Json.writes[UnitTestTestData]

  def readTestCorrectionResultFile(targetFile: File): Try[Seq[UnitTestCorrectionResult]] =
    Try(Json.parse(targetFile.contentAsString)).flatMap { jsValue =>
      unitTestCorrectionResultsFileJsonReads.reads(jsValue) match {
        case JsSuccess(result, _) => Success(result.results)
        case JsError(errors)      =>
          errors.foreach(error => logger.error(s"There has been an error reading a json programming result file: $error"))
          Failure(new Exception("There has been an error reading a json programming result file!"))
      }
    }

}
