package model.programming

import model.programming.ProgDataTypes.ProgDataType
import play.api.libs.json.{JsArray, JsString, JsValue, Json}

object TestDataJsonFormat {

  def dumpTestDataToJson(exercise: ProgCompleteEx, testData: Seq[CompleteTestData]): JsValue = {
    val sortedInputTypes: Seq[ProgInput] = exercise.inputTypes sortBy (_.id)

    Json.obj(
      "functionname" -> JsString(exercise.ex.functionName),
      "variableTypes" -> JsArray(sortedInputTypes map (_.inputType.typeName) map JsString),
      "outputType" -> exercise.ex.outputType.typeName,
      "testdata" -> dumpTestData(testData, sortedInputTypes, exercise.ex.outputType)
    )
  }

  private def dumpTestData(testData: Seq[CompleteTestData], sortedInputTypes: Seq[ProgInput], outputType: ProgDataType) =
    JsArray(testData sortBy (_.testData.id) map { td =>

      val inputs: Seq[JsValue] = td.inputs zip sortedInputTypes map {
        case (sampleTestData, dataType) => dataType.inputType.toJson(sampleTestData.input)
      }

      Json.obj(
        "id" -> td.testData.id,
        "inputs" -> JsArray(inputs),
        "awaited" -> outputType.toJson(td.testData.output)
      )
    })

}
