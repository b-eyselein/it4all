package model.programming

import model.MyYamlProtocol._
import model.programming.ProgConsts._
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

import scala.language.{implicitConversions, postfixOps}

object ProgExYamlProtocol extends MyYamlProtocol {

  implicit object ProgExYamlFormat extends HasBaseValuesYamlFormat[ProgCompleteEx] {

    override def readRest(yamlObject: YamlObject, baseValues: BaseValues): ProgCompleteEx = ProgCompleteEx(
      ProgExercise(baseValues,
        yamlObject.stringField(FUNCTIONNAME_NAME),
        yamlObject.intField(INPUTCOUNT_NAME)
      ),
      yamlObject.objectField(SAMPLE_SOL_NAME, ProgSampleSolutionYamlFormat(baseValues.id)),
      yamlObject.arrayField(SAMPLE_TESTDATA_NAME, _ convertTo[CompleteSampleTestData] ProgCompleteSampleTestdataYamlFormat(baseValues.id))
    )

    override protected def writeRest(completeEx: ProgCompleteEx): Map[YamlValue, YamlValue] = Map(
      YamlString(FUNCTIONNAME_NAME) -> completeEx.ex.functionName,
      YamlString(INPUTCOUNT_NAME) -> completeEx.ex.inputCount,
      YamlString(SAMPLE_SOL_NAME) -> completeEx.sampleSolution.toYaml(ProgSampleSolutionYamlFormat(completeEx.ex.id)),
      YamlString(SAMPLE_TESTDATA_NAME) -> YamlArray(completeEx.sampleTestData map (_ toYaml ProgCompleteSampleTestdataYamlFormat(completeEx.ex.id)) toVector)
    )
  }

  case class ProgSampleSolutionYamlFormat(exerciseId: Int) extends MyYamlFormat[ProgSampleSolution] {

    override def readObject(yamlObject: YamlObject): ProgSampleSolution = ProgSampleSolution(
      exerciseId, yamlObject.enumField(LANGUAGE_NAME, ProgLanguage.valueOf(_).get, ProgLanguage.STANDARD_LANG), yamlObject.stringField(SAMPLE_NAME)
    )

    override def write(pss: ProgSampleSolution): YamlValue = YamlObject(
      YamlString(LANGUAGE_NAME) -> pss.language.name,
      YamlString(SAMPLE_NAME) -> pss.solution
    )

  }

  case class ProgCompleteSampleTestdataYamlFormat(exerciseId: Int) extends MyYamlFormat[CompleteSampleTestData] {

    override def readObject(yamlObject: YamlObject): CompleteSampleTestData = {
      val id = yamlObject.intField(ID_NAME)
      CompleteSampleTestData(
        SampleTestData(id, exerciseId, yamlObject.forgivingStringField(OUTPUT_NAME)),
        yamlObject.arrayField(INPUTS_NAME, _ convertTo[SampleTestDataInput] TestDataInputYamlFormat(id, exerciseId))
      )
    }

    override def write(cstd: CompleteSampleTestData): YamlValue = YamlObject(
      YamlString(ID_NAME) -> cstd.sampleTestData.id,
      YamlString(OUTPUT_NAME) -> cstd.sampleTestData.output,
      YamlString(INPUTS_NAME) -> YamlArray(cstd.inputs map (_ toYaml TestDataInputYamlFormat(cstd.sampleTestData.id, cstd.sampleTestData.exerciseId)) toVector)
    )

  }

  case class TestDataInputYamlFormat(testId: Int, exerciseId: Int) extends MyYamlFormat[SampleTestDataInput] {

    override def readObject(yamlObject: YamlObject): SampleTestDataInput = SampleTestDataInput(
      yamlObject.intField(ID_NAME), testId, exerciseId, yamlObject.forgivingStringField(INPUT_NAME)
    )

    override def write(stdi: SampleTestDataInput): YamlValue = YamlObject(YamlString(ID_NAME) -> stdi.id, YamlString(INPUT_NAME) -> stdi.input)

  }

}
