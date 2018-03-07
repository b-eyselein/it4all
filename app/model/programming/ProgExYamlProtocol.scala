package model.programming

import model.Enums.ExerciseState
import model.MyYamlProtocol._
import model.programming.ProgConsts._
import model.{MyYamlProtocol, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.implicitConversions
import scala.util.{Failure, Try}

object ProgExYamlProtocol extends MyYamlProtocol {

  implicit object ProgExYamlFormat extends HasBaseValuesYamlFormat[ProgCompleteEx] {

    override def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[ProgCompleteEx] = for {
      functionName <- yamlObject.stringField(FunctionName)
      outputType <- yamlObject.enumField(OutputTypeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

      inputTypeNames: (Seq[String], Seq[Failure[String]]) <- yamlObject.arrayField(InputTypesName, _.asStr)

      sampleSolution <- yamlObject.someField(SAMPLE_SOL_NAME) flatMap ProgSampleSolutionYamlFormat(baseValues._1).read
      sampleTestDataTries <- yamlObject.arrayField(SAMPLE_TESTDATA_NAME, ProgCompleteSampleTestdataYamlFormat(baseValues._1).read)
    } yield {
      for (sampleTdFailure <- sampleTestDataTries._2)
      // FIXME: return...
        Logger.error("Could not read sample test data", sampleTdFailure.exception)

      for (inputTypeFailure <- inputTypeNames._2)
      // FIXME: ...
        Logger.error("Could not read input type name ", inputTypeFailure.exception)

      val inputTypes: Seq[InputType] = inputTypeNames._1.zipWithIndex.map {
        case (str, index) => InputType(index, baseValues._1, ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
      }

      ProgCompleteEx(new ProgExercise(baseValues, functionName, outputType), inputTypes, sampleSolution, sampleTestDataTries._1)
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
      language <- yamlObject.enumField(LanguageName, ProgLanguage.valueOf) map (_ getOrElse PYTHON_3)
      sample <- yamlObject.stringField(SAMPLE_NAME)
    } yield ProgSampleSolution(exerciseId, language, sample)

    override def write(pss: ProgSampleSolution): YamlValue = YamlObj(
      LanguageName -> pss.language.name,
      SAMPLE_NAME -> pss.solution
    )

  }

  case class ProgCompleteSampleTestdataYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[CompleteSampleTestData] {

    override def readObject(yamlObject: YamlObject): Try[CompleteSampleTestData] = for {
      id <- yamlObject.intField(idName)
      output <- yamlObject.forgivingStringField(OUTPUT_NAME)
      inputTries <- yamlObject.arrayField(InputsName, TestDataInputYamlFormat(id, exerciseId).read)
    } yield {
      for (inputFailure <- inputTries._2)
      // FIXME: return...
        Logger.error("Could not read prog test data input", inputFailure.exception)

      CompleteSampleTestData(SampleTestData(id, exerciseId, output), inputTries._1)
    }

    override def write(cstd: CompleteSampleTestData): YamlValue = YamlObj(
      idName -> cstd.testData.id,
      OUTPUT_NAME -> cstd.testData.output,
      InputsName -> YamlArr(cstd.inputs map TestDataInputYamlFormat(cstd.testData.id, cstd.testData.exerciseId).write)
    )

  }

  case class TestDataInputYamlFormat(testId: Int, exerciseId: Int) extends MyYamlObjectFormat[SampleTestDataInput] {

    override def readObject(yamlObject: YamlObject): Try[SampleTestDataInput] = for {
      id <- yamlObject.intField(idName)
      newInput: String <- yamlObject.someField(InputName) map {
        case YamlArray(values) => values map (v => v.forgivingStr) mkString ", "
        case other             => other.forgivingStr
      }
    } yield {
      SampleTestDataInput(id, testId, exerciseId, newInput)
    }

    override def write(stdi: SampleTestDataInput): YamlValue = YamlObj(idName -> stdi.id, InputName -> stdi.input)

  }

}
