package model.programming

import model.programming.ProgConsts._
import model.programming.ProgDataTypes.ProgDataType
import play.api.libs.json._

object TestDataJsonFormat {

  def dumpTestDataToJson(exercise: ProgCompleteEx, testData: Seq[CompleteTestData]): JsValue = Json.obj(
    functionNameName -> JsString(exercise.ex.functionname),
    "testdata" -> dumpTestData(testData, exercise.inputTypes sortBy (_.id), exercise.ex.outputType)
  )

  // FIXME: how to display input? ==> with variable name!!
  private def dumpTestData(testData: Seq[CompleteTestData], sortedInputs: Seq[ProgInput], outputType: ProgDataType) =
    JsArray(testData sortBy (_.testData.id) map { td =>
      Json.obj(
        idName -> td.testData.id,
        inputName -> td.inputs.sortBy(_.id).map(sampleTestData => Json.parse(sampleTestData.input)),
        outputName -> outputType.toJson(td.testData.output)
      )
    })

}
