package model.programming

import model.MyYamlProtocol._
import model.programming.ProgConsts._
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

import scala.language.{implicitConversions, postfixOps}

object ProgExYamlProtocol extends MyYamlProtocol {

  implicit object ProgExYamlFormat extends ExYamlFormat[ProgCompleteEx] {

    override def readRest(yamlObject: YamlObject, baseValues: BaseValues): ProgCompleteEx = ProgCompleteEx(
      ProgExercise(baseValues, yamlObject.stringField(FUNCTIONNAME_NAME), yamlObject.intField(INPUTCOUNT_NAME)),
      yamlObject.arrayField(SAMPLE_TESTDATA_NAME, _ convertTo[CompleteSampleTestData] ProgCompleteSampleTestdataYamlFormat(baseValues.id))
    )

    override def write(obj: ProgCompleteEx): YamlValue = ???

  }

  case class ProgCompleteSampleTestdataYamlFormat(exerciseId: Int) extends MyYamlFormat[CompleteSampleTestData] {

    override def readObject(yamlObject: YamlObject): CompleteSampleTestData = {
      val id = yamlObject.intField(ID_NAME)
      CompleteSampleTestData(
        SampleTestData(id, exerciseId, yamlObject.stringField(OUTPOUT_NAME)),
        yamlObject.arrayField(INPUTS_NAME, _ convertTo[SampleTestDataInput] TestDataInputYamlFormat(id, exerciseId))
      )
    }

    override def write(obj: CompleteSampleTestData): YamlValue = ???

  }

  case class TestDataInputYamlFormat(exerciseId: Int, testId: Int) extends MyYamlFormat[SampleTestDataInput] {

    override def readObject(yamlObject: YamlObject): SampleTestDataInput = SampleTestDataInput(
      yamlObject.intField(ID_NAME), testId, exerciseId, yamlObject.stringField(INPUT_NAME)
    )

    override def write(obj: SampleTestDataInput): YamlValue = ???

  }

}
