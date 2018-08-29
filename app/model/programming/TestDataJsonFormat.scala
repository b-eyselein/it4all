package model.programming

import model.programming.ProgConsts._
import model.programming.ProgDataTypes.ProgDataType
import play.api.libs.json._

object TestDataJsonFormat {

  def dumpTestDataToJson(exercise: ProgCompleteEx, testData: Seq[TestData]): JsValue = JsObject(
    Seq(
      functionNameName -> JsString(exercise.ex.functionname),
      testdataName -> dumpTestData(testData, exercise.inputTypes sortBy (_.id), exercise.ex.outputType)
    ) ++ exercise.ex.baseData.map(x => baseDataName -> x)
  )

  // FIXME: how to display input? ==> with variable name!!
  private def dumpTestData(testData: Seq[TestData], sortedInputs: Seq[ProgInput], outputType: ProgDataType): JsValue =
    JsArray(testData sortBy (_.id) map { td =>
      Json.obj(
        idName -> td.id,
        inputName -> td.inputAsJson,
        outputName -> td.output
      )
    })

}
