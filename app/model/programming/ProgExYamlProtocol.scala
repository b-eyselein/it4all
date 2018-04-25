package model.programming

import java.nio.file.Paths

import model.ExerciseState
import model.MyYamlProtocol._
import model.core.FileUtils
import model.programming.ProgConsts._
import model.uml.UmlClassDiagram
import model.uml.UmlExYamlProtocol.UmlSolutionYamlFormat
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
      baseData <- yamlObject.optJsonField(baseDataName)

      inputTypes <- yamlObject.arrayField(inputTypesName, ProgInputTypeYamlFormat(baseValues._1).read)
      sampleSolutions <- yamlObject.arrayField(sampleSolutionsName, ProgSampleSolutionYamlFormat(baseValues._1).read)
      sampleTestDataTries <- yamlObject.arrayField(sampleTestDataName, ProgSampleTestdataYamlFormat(baseValues._1).read)

      maybeClassDiagramPart <- yamlObject.optField(classDiagramPartName, UmlClassDiagPartYamlFormat(baseValues._1).read)
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
        ProgExercise(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, folderIdentifier, base, functionname, indentLevel, outputType, baseData),
        inputTypes._1, sampleSolutions._1, sampleTestDataTries._1, maybeClassDiagramPart
      )
    }

    override protected def writeRest(completeEx: ProgCompleteEx): Map[YamlValue, YamlValue] = Map(
      YamlString(functionNameName) -> completeEx.ex.functionname,
      YamlString(inputTypesName) -> YamlArr(completeEx.inputTypes.map(it => YamlString(it.inputType.typeName))),
      YamlString(sampleSolutionsName) -> YamlArr(completeEx.sampleSolutions map ProgSampleSolutionYamlFormat(completeEx.ex.id).write),
      YamlString(sampleTestDataName) -> YamlArr(completeEx.sampleTestData map ProgSampleTestdataYamlFormat(completeEx.ex.id).write)
    )
  }

  case class UmlClassDiagPartYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[UmlClassDiagPart] {

    override protected def readObject(yamlObject: YamlObject): Try[UmlClassDiagPart] = for {
      className <- yamlObject.stringField(classNameName)
      classDiagram: UmlClassDiagram <- yamlObject.someField(classDiagramName).flatMap(UmlSolutionYamlFormat.read)
    } yield UmlClassDiagPart(exerciseId, className, classDiagram)

    override def write(obj: UmlClassDiagPart): YamlValue = ???

  }

  case class ProgInputTypeYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[ProgInput] {

    override protected def readObject(yamlObject: YamlObject): Try[ProgInput] = for {
      id <- yamlObject.intField(idName)
      inputName <- yamlObject.stringField(nameName)
      inputType <- yamlObject.enumField(typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
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

  case class ProgSampleTestdataYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[SampleTestData] {

    override def readObject(yamlObject: YamlObject): Try[SampleTestData] = for {
      id <- yamlObject.intField(idName)
      output <- yamlObject.forgivingStringField(outputName)
      inputAsJson <- yamlObject.jsonField(inputsName)
    } yield SampleTestData(id, exerciseId, inputAsJson, output)

    override def write(cstd: SampleTestData): YamlValue = YamlObj(
      idName -> cstd.id,
      outputName -> cstd.output,
      inputsName -> cstd.inputAsJson.toString()
    )

  }

}
