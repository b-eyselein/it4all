package model.programming

import java.nio.file.{Path, Paths}

import model.MyYamlProtocol._
import model.programming.ProgConsts._
import model.uml.UmlClassDiagram
import model.uml.UmlExYamlProtocol.UmlSampleSolutionYamlFormat
import model.{BaseValues, MyYamlProtocol, SemanticVersion, SemanticVersionHelper, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.implicitConversions
import scala.util.Try

object ProgExYamlProtocol extends MyYamlProtocol {

  val basePath: Path = Paths.get("conf", "resources", "programming")

  implicit object ProgExYamlFormat extends MyYamlObjectFormat[ProgExercise] {

    override def readObject(yamlObject: YamlObject): Try[ProgExercise] = for {
      baseValues <- readBaseValues(yamlObject)
      folderIdentifier <- yamlObject.stringField(identifierName)

      functionName <- yamlObject.stringField(functionNameName)
      outputType <- yamlObject.enumField(outputTypeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
      baseData <- yamlObject.optJsonField(baseDataName)

      inputTypes <- yamlObject.arrayField(inputTypesName, ProgInputTypeYamlFormat(baseValues).read)
      sampleSolutions <- yamlObject.arrayField(sampleSolutionsName, ProgSampleSolutionYamlFormat(baseValues.id, baseValues.semanticVersion, folderIdentifier).read)
      sampleTestDataTries <- yamlObject.arrayField(sampleTestDataName, ProgSampleTestdataYamlFormat(baseValues).read)

      maybeClassDiagramPart <- yamlObject.optField(classDiagramPartName, UmlClassDiagPartYamlFormat(baseValues).read)
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

      ProgExercise(
        baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
        folderIdentifier, functionName, outputType, baseData,
        inputTypes._1, sampleSolutions._1, sampleTestDataTries._1, maybeClassDiagramPart
      )
    }

    override def write(exercise: ProgExercise): YamlObject = {
      val progSampleSolYamlFormat = ProgSampleSolutionYamlFormat(exercise.id, exercise.semanticVersion, exercise.folderIdentifier)
      YamlObject(
        writeBaseValues(exercise.baseValues) ++
          Map[YamlValue, YamlValue](
            YamlString(functionNameName) -> YamlString(exercise.functionName),
            YamlString(inputTypesName) -> YamlArr(exercise.inputTypes.map(it => YamlString(it.inputType.typeName))),
            YamlString(sampleSolutionsName) -> YamlArr(exercise.sampleSolutions map progSampleSolYamlFormat.write),
            YamlString(sampleTestDataName) -> YamlArr(exercise.sampleTestData map ProgSampleTestdataYamlFormat(exercise.baseValues).write)
          )
      )
    }

  }

  final case class UmlClassDiagPartYamlFormat(baseValues: BaseValues) extends MyYamlObjectFormat[UmlClassDiagPart] {

    override protected def readObject(yamlObject: YamlObject): Try[UmlClassDiagPart] = for {
      className <- yamlObject.stringField(classNameName)
      // FIXME: reverse to only UmlClassDiagram
      classDiagram: UmlClassDiagram <- yamlObject.someField(classDiagramName).flatMap(UmlSampleSolutionYamlFormat(0, SemanticVersionHelper.DEFAULT).read).map(_.sample)
    } yield UmlClassDiagPart(baseValues.id, baseValues.semanticVersion, className, classDiagram)

    override def write(obj: UmlClassDiagPart): YamlValue = ???

  }

  final case class ProgInputTypeYamlFormat(baseValues: BaseValues) extends MyYamlObjectFormat[ProgInput] {

    override protected def readObject(yamlObject: YamlObject): Try[ProgInput] = for {
      id <- yamlObject.intField(idName)
      inputName <- yamlObject.stringField(nameName)
      inputType <- yamlObject.enumField(typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
    } yield ProgInput(id, baseValues.id, baseValues.semanticVersion, inputName, inputType)

    override def write(obj: ProgInput): YamlValue = ???

  }

  final case class ProgSampleSolutionYamlFormat(exId: Int, exSemVer: SemanticVersion, folderIdentifier: String) extends MyYamlObjectFormat[ProgSampleSolution] {

    override def readObject(yamlObject: YamlObject): Try[ProgSampleSolution] = for {
      id <- yamlObject.intField(idName)
      language <- yamlObject.enumField(languageName, ProgLanguages.withNameInsensitiveOption) map (_ getOrElse ProgLanguages.PYTHON_3)
      base <- yamlObject.stringField(baseName)
      sample <- yamlObject.stringField(sampleName)
    } yield ProgSampleSolution(id, exId, exSemVer, language, base, sample)

    override def write(pss: ProgSampleSolution): YamlValue = YamlObj(
      languageName -> pss.language.entryName,
      sampleName -> pss.sample.solution
    )

  }

  final case class ProgSampleTestdataYamlFormat(baseValues: BaseValues) extends MyYamlObjectFormat[SampleTestData] {

    override def readObject(yamlObject: YamlObject): Try[SampleTestData] = for {
      id <- yamlObject.intField(idName)
      output <- yamlObject.jsonField(outputName)
      inputAsJson <- yamlObject.jsonField(inputsName)
    } yield SampleTestData(id, baseValues.id, baseValues.semanticVersion, inputAsJson, output)

    override def write(cstd: SampleTestData): YamlValue = YamlObj(
      idName -> cstd.id,
      outputName -> cstd.output.toString(),
      inputsName -> cstd.inputAsJson.toString()
    )

  }

}
