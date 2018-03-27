package model.programming

import model.programming.ProgDataTypes.ProgDataType
import play.api.libs.json.{JsArray, JsString, JsValue, Json}

object TestDataJsonFormat {

  def dumpTestDataToJson(exercise: ProgExercise, inputTypes: Seq[ProgInput], testData: Seq[CompleteTestData]): JsValue = {
    val sortedInputTypes: Seq[ProgInput] = inputTypes sortBy (_.id)

    Json.obj(
      "functionname" -> JsString(exercise.functionName),
      "variableTypes" -> JsArray(sortedInputTypes map (_.inputType.typeName) map JsString),
      "outputType" -> exercise.outputType.typeName,
      "testdata" -> dumpTestData(testData, sortedInputTypes, exercise.outputType)
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
