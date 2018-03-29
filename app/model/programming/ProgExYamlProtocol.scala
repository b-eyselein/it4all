package model.programming

import java.nio.file.Paths

import model.Enums.ExerciseState
import model.MyYamlProtocol._
import model.core.FileUtils
import model.programming.ProgConsts._
import model.{MyYamlProtocol, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.implicitConversions
import scala.util.Try

object ProgExYamlProtocol extends MyYamlProtocol {

  implicit object ProgExYamlFormat extends HasBaseValuesYamlFormat[ProgCompleteEx] with FileUtils {

    override def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[ProgCompleteEx] = for {
      folderIdentifier <- yamlObject.stringField(identifierName)

      base <- readAll(Paths.get("conf", "resources", "programming", baseValues._1 + "-" + folderIdentifier, "base.py"))

      className <- yamlObject.optStringField(classNameName)
      functionname <- yamlObject.stringField(functionNameName)
      indentLevel <- yamlObject.intField(indentLevelName)
      outputType <- yamlObject.enumField(outputTypeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

      inputTypes <- yamlObject.arrayField(inputTypesName, ProgInputTypeYamlFormat(baseValues._1).read)
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

      ProgCompleteEx(
        ProgExercise(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, folderIdentifier, base, className, functionname, indentLevel, outputType),
        inputTypes._1, sampleSolutions._1, sampleTestDataTries._1
      )
    }

    override protected def writeRest(completeEx: ProgCompleteEx): Map[YamlValue, YamlValue] = Map(
      YamlString(functionNameName) -> completeEx.ex.functionname,
      YamlString(inputTypesName) -> YamlArr(completeEx.inputTypes.map(it => YamlString(it.inputType.typeName))),
      YamlString(sampleSolutionsName) -> YamlArr(completeEx.sampleSolutions map ProgSampleSolutionYamlFormat(completeEx.ex.id).write),
      YamlString(sampleTestDataName) -> YamlArr(completeEx.sampleTestData map ProgCompleteSampleTestdataYamlFormat(completeEx.ex.id).write)
    )
  }

  case class ProgInputTypeYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[ProgInput] {

    override protected def readObject(yamlObject: YamlObject): Try[ProgInput] = for {
      id <- yamlObject.intField(idName)
      inputName <- yamlObject.stringField(nameName)
      inputType <- yamlObject.enumField(inputTypeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
    } yield ProgInput(id, exerciseId, inputName, inputType)

    override def write(obj: ProgInput): YamlValue = ???

  }

  case class ProgSampleSolutionYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[ProgSampleSolution] {

    override def readObject(yamlObject: YamlObject): Try[ProgSampleSolution] = for {
      language <- yamlObject.enumField(languageName, ProgLanguage.valueOf) map (_ getOrElse PYTHON_3)
      base <- yamlObject.stringField(baseName)
      sample <- yamlObject.stringField(sampleName)
    } yield ProgSampleSolution(exerciseId, language, base, sample)

    override def write(pss: ProgSampleSolution): YamlValue = YamlObj(
      languageName -> pss.language.name,
      sampleName -> pss.solution
    )

  }

  case class ProgCompleteSampleTestdataYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[CompleteSampleTestData] {

    override def readObject(yamlObject: YamlObject): Try[CompleteSampleTestData] = for {
      id <- yamlObject.intField(idName)
      output <- yamlObject.forgivingStringField(outputName)
      inputTries <- yamlObject.arrayField(inputsName, TestDataInputYamlFormat(id, exerciseId).read)
    } yield {
      for (inputFailure <- inputTries._2)
      // FIXME: return...
        Logger.error("Could not read prog test data input", inputFailure.exception)

      CompleteSampleTestData(SampleTestData(id, exerciseId, output), inputTries._1)
    }

    override def write(cstd: CompleteSampleTestData): YamlValue = YamlObj(
      idName -> cstd.testData.id,
      outputName -> cstd.testData.output,
      inputsName -> YamlArr(cstd.inputs map TestDataInputYamlFormat(cstd.testData.id, cstd.testData.exerciseId).write)
    )

  }

  case class TestDataInputYamlFormat(testId: Int, exerciseId: Int) extends MyYamlObjectFormat[SampleTestDataInput] {

    override def readObject(yamlObject: YamlObject): Try[SampleTestDataInput] = for {
      id <- yamlObject.intField(idName)
      input <- yamlObject.someField(inputName).map(mapToJson)
    } yield SampleTestDataInput(id, testId, exerciseId, input)

    override def write(stdi: SampleTestDataInput): YamlValue = YamlObj(idName -> stdi.id, inputName -> stdi.input)

    private def mapToJson(yamlValue: YamlValue): String = yamlValue match {
      case YamlArray(yamlValues)  => "[" + (yamlValues map mapToJson mkString ",") + "]"
      case YamlObject(yamlFields) => "{" + (yamlFields mkString ",") + "}"
      case YamlString(str)        => "\"" + str + "\""
      case YamlBoolean(bool)      => bool.toString
      case YamlNull               => "null"
      case YamlNumber(bigDecimal) => bigDecimal.intValue().toString

      // TODO: other yamlValues such as YamlPosInf, YamlNegInf, ...
      case other => other.toString
    }

  }

}
