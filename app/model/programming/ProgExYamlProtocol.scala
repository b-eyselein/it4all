package model.programming

import model.Enums.ExerciseState
import model.MyYamlProtocol._
import model.programming.ProgConsts._
import model.{MyYamlProtocol, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.implicitConversions
import scala.util.Try

object ProgExYamlProtocol extends MyYamlProtocol {

  implicit object ProgExYamlFormat extends HasBaseValuesYamlFormat[ProgCompleteEx] {

    override def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[ProgCompleteEx] = for {
      functionName <- yamlObject.stringField(FunctionName)
      outputType <- yamlObject.enumField(outputTypeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

      inputTypes <- yamlObject.arrayField(InputTypesName, ProgInpuptTypeYamlFormat(baseValues._1).read)

      sampleSolutions <- yamlObject.arrayField(sampleSolutionsName, ProgSampleSolutionYamlFormat(baseValues._1).read)
      sampleTestDataTries <- yamlObject.arrayField(sampleTestDataName, ProgCompleteSampleTestdataYamlFormat(baseValues._1).read)
    } yield {
      for (sampleTdFailure <- sampleTestDataTries._2)
      // FIXME: return...
        Logger.error("Could not read sample test data", sampleTdFailure.exception)

      for (inputTypeFailure <- inputTypes._2)
      // FIXME: return ...
        Logger.error("Could not read input type name ", inputTypeFailure.exception)

      for (sampleSolutionFailure <- sampleSolutions._2)
      // FIXME: return ...
        Logger.error("Could not read programming sample solution", sampleSolutionFailure.exception)

      ProgCompleteEx(new ProgExercise(baseValues, functionName, outputType), inputTypes._1, sampleSolutions._1, sampleTestDataTries._1)
    }

    override protected def writeRest(completeEx: ProgCompleteEx): Map[YamlValue, YamlValue] = Map(
      YamlString(FunctionName) -> completeEx.ex.functionName,
      YamlString(InputTypesName) -> YamlArr(completeEx.inputTypes.map(it => YamlString(it.inputType.typeName))),
      YamlString(sampleSolutionsName) -> YamlArr(completeEx.sampleSolutions map ProgSampleSolutionYamlFormat(completeEx.ex.id).write),
      YamlString(sampleTestDataName) -> YamlArr(completeEx.sampleTestData map ProgCompleteSampleTestdataYamlFormat(completeEx.ex.id).write)
    )
  }

  case class ProgInpuptTypeYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[ProgInput] {

    override protected def readObject(yamlObject: YamlObject): Try[ProgInput] = for {
      id <- yamlObject.intField(idName)
      inputName <- yamlObject.stringField(nameName)
      inputType <- yamlObject.enumField(InputTypeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
    } yield ProgInput(id, exerciseId, inputName, inputType)

    override def write(obj: ProgInput): YamlValue = ???

  }

  case class ProgSampleSolutionYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[ProgSampleSolution] {

    override def readObject(yamlObject: YamlObject): Try[ProgSampleSolution] = for {
      language <- yamlObject.enumField(LanguageName, ProgLanguage.valueOf) map (_ getOrElse PYTHON_3)
      base <- yamlObject.stringField(baseName)
      sample <- yamlObject.stringField(sampleName)
      testMain <- yamlObject.stringField(testMainName)
    } yield ProgSampleSolution(exerciseId, language, base, sample, testMain)

    override def write(pss: ProgSampleSolution): YamlValue = YamlObj(
      LanguageName -> pss.language.name,
      sampleName -> pss.solution
    )

  }

  case class ProgCompleteSampleTestdataYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[CompleteSampleTestData] {

    override def readObject(yamlObject: YamlObject): Try[CompleteSampleTestData] = for {
      id <- yamlObject.intField(idName)
      output <- yamlObject.forgivingStringField(outputName)
      inputTries <- yamlObject.arrayField(InputsName, TestDataInputYamlFormat(id, exerciseId).read)
    } yield {
      for (inputFailure <- inputTries._2)
      // FIXME: return...
        Logger.error("Could not read prog test data input", inputFailure.exception)

      CompleteSampleTestData(SampleTestData(id, exerciseId, output), inputTries._1)
    }

    override def write(cstd: CompleteSampleTestData): YamlValue = YamlObj(
      idName -> cstd.testData.id,
      outputName -> cstd.testData.output,
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
