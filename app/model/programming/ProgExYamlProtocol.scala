package model.programming

import model.MyYamlProtocol._
import model.programming.ProgConsts._
import model.{BaseValues, MyYamlProtocol, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object ProgExYamlProtocol extends MyYamlProtocol {

  implicit object ProgExYamlFormat extends HasBaseValuesYamlFormat[ProgCompleteEx] {

    override def readRest(yamlObject: YamlObject, baseValues: BaseValues): Try[ProgCompleteEx] = for {
      functionName <- yamlObject.stringField(FunctionName)
      outputType <- yamlObject.enumField(OutputTypeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

      inputTypes: Seq[InputType] = Seq.empty
      //      yamlObject.arrayField(InputTypesName, _.asStr).zipWithIndex.map {
      //        case (optStr, index) => InputType(index, baseValues.id, optStr flatMap (str => ProgDataTypes.byName(str)) getOrElse ProgDataTypes.STRING)
      //      }

      sampleSolution <- yamlObject.someField(SAMPLE_SOL_NAME) flatMap ProgSampleSolutionYamlFormat(baseValues.id).read
      sampleTestDataTries <- yamlObject.arrayField(SAMPLE_TESTDATA_NAME, ProgCompleteSampleTestdataYamlFormat(baseValues.id).read)
    } yield {
      for (sampleTdFailure <- sampleTestDataTries._2)
      // FIXME: return...
        Logger.error("Could not read sample test data", sampleTdFailure.exception)

      ProgCompleteEx(ProgExercise(baseValues, functionName, outputType), inputTypes, sampleSolution, sampleTestDataTries._1)
    }

    override protected def writeRest(completeEx: ProgCompleteEx): Map[YamlValue, YamlValue] = Map(
      YamlString(FunctionName) -> completeEx.ex.functionName,
      YamlString(InputTypesName) -> YamlArr(completeEx.inputTypes.map(it => YamlString(it.inputType.typeName))),
      YamlString(SAMPLE_SOL_NAME) -> ProgSampleSolutionYamlFormat(completeEx.ex.id).write(completeEx.sampleSolution),
      YamlString(SAMPLE_TESTDATA_NAME) -> YamlArr(completeEx.sampleTestData map ProgCompleteSampleTestdataYamlFormat(completeEx.ex.id).write)
    )
  }

  case class ProgSampleSolutionYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[ProgSampleSolution] {

    override def readObject(yamlObject: YamlObject): Try[ProgSampleSolution] = for {
      language <- yamlObject.enumField(LANGUAGE_NAME, ProgLanguage.valueOf(_).get)
      sample <- yamlObject.stringField(SAMPLE_NAME)
    } yield ProgSampleSolution(exerciseId, language, sample)

    override def write(pss: ProgSampleSolution): YamlValue = YamlObj(
      LANGUAGE_NAME -> pss.language.name,
      SAMPLE_NAME -> pss.solution
    )

  }

  case class ProgCompleteSampleTestdataYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[CompleteSampleTestData] {

    override def readObject(yamlObject: YamlObject): Try[CompleteSampleTestData] = for {
      id <- yamlObject.intField(ID_NAME)
      output <- yamlObject.forgivingStringField(OUTPUT_NAME)
      inputTries <- yamlObject.arrayField(INPUTS_NAME, TestDataInputYamlFormat(id, exerciseId).read)
    } yield {
      for (inputFailure <- inputTries._2)
      // FIXME: return...
        Logger.error("Could not read prog test data input", inputFailure.exception)

      CompleteSampleTestData(SampleTestData(id, exerciseId, output), inputTries._1)
    }

    override def write(cstd: CompleteSampleTestData): YamlValue = YamlObj(
      ID_NAME -> cstd.testData.id,
      OUTPUT_NAME -> cstd.testData.output,
      INPUTS_NAME -> YamlArr(cstd.inputs map TestDataInputYamlFormat(cstd.testData.id, cstd.testData.exerciseId).write)
    )

  }

  case class TestDataInputYamlFormat(testId: Int, exerciseId: Int) extends MyYamlObjectFormat[SampleTestDataInput] {

    override def readObject(yamlObject: YamlObject): Try[SampleTestDataInput] = for {
      id <- yamlObject.intField(ID_NAME)
      newInput: String <- yamlObject.someField(INPUT_NAME) map {
        case YamlArray(values) => values map (v => v.forgivingStr) mkString ", "
        case other             => other.forgivingStr
      }
    } yield SampleTestDataInput(id, testId, exerciseId, newInput)

    override def write(stdi: SampleTestDataInput): YamlValue = YamlObj(ID_NAME -> stdi.id, INPUT_NAME -> stdi.input)

  }

}
