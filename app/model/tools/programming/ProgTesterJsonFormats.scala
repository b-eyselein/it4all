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
      functionNameName -> JsString(exercise.functionName),
      "test_data" -> dumpTestData(testData, exercise.inputTypes sortBy (_.id), exercise.outputType),
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

final case class ResultFileContent(resultType: String, results: Seq[ExecutionResult], errors: String)

//noinspection ConvertibleToMethodValue
object ResultsFileJsonFormat {

  private implicit val executionResultJsonReads: Reads[ExecutionResult] = (
    (__ \ "success").read[SuccessType] and
      (__ \ "test_id").read[Int] and
      (__ \ "test_input").read[JsValue] and
      (__ \ "awaited").read[JsValue] and
      (__ \ "gotten").read[JsValue] and
      (__ \ "stdout").readNullable[String]
    ) (ExecutionResult.apply(_, _, _, _, _, _))

  private val resultsFileJsonReads: Reads[ResultFileContent] = (
    (__ \ "result_type").read[String] and
      (__ \ "results").read[Seq[ExecutionResult]] and
      (__ \ "errors").read[String]
    ) (ResultFileContent.apply(_, _, _))

  def readResultFile(targetFile: File, completeTestData: Seq[ProgTestData]): Try[Seq[ExecutionResult]] = {

    resultsFileJsonReads.reads(Json.parse(targetFile.contentAsString)) match {
      case JsSuccess(result: ResultFileContent, _) =>
        Success(
          result.results)

      case JsError(errors) =>
        errors.foreach(error => Logger.error("There has been an error reading a json programming result file: " + error))
        Failure(null)
    }
  }


}
