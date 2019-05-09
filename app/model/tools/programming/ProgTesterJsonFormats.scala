package model.tools.programming

import better.files.File
import model.core.result.SuccessType
import model.tools.programming.ProgConsts._
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

object TestDataJsonFormat {

  def dumpTestDataToJson(exercise: ProgExercise, testData: Seq[ProgTestData]): JsValue = Json.obj(
    simplifiedName -> Json.obj(
      testDataName -> dumpTestData(testData, exercise.inputTypes sortBy (_.id), exercise.outputType),
      baseDataName -> exercise.baseData
    ),
    extendedName -> JsNull
  )

  // FIXME: how to display input? ==> with variable name!!
  private def dumpTestData(testData: Seq[ProgTestData], sortedInputs: Seq[ProgInput], outputType: ProgDataType): JsValue =
    JsArray(testData sortBy (_.id) map { td =>
      Json.obj(
        idName -> td.id,
        inputName -> td.inputAsJson,
        outputName -> td.output
      )
    })

}

final case class UnitTestTestData(foldername: String, filename: String, testConfigs: Seq[UnitTestTestConfig])

//noinspection ConvertibleToMethodValue
object ResultsFileJsonFormat {

  private val logger = Logger(ResultsFileJsonFormat.getClass)

  // TODO: Json.reads[ExecutionResult]
  private implicit val executionResultJsonReads: Reads[ExecutionResult] = (
    (__ \ "success").read[SuccessType] and
      (__ \ "testId").read[Int] and
      (__ \ "testInput").read[JsValue] and
      (__ \ "awaited").read[JsValue] and
      (__ \ "gotten").read[JsValue] and
      (__ \ "stdout").readNullable[String]
    ) (ExecutionResult.apply(_, _, _, _, _, _))

  // TODO: Json.reads[ResultFileContent]
  private val resultsFileJsonReads: Reads[ResultFileContent] = Json.reads[ResultFileContent]

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
