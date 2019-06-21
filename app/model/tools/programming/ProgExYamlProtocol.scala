package model.tools.programming

import java.nio.file.{Path, Paths}

import model.MyYamlProtocol._
import model.tools.programming.ProgConsts._
import model.tools.uml.UmlExYamlProtocol.UmlSampleSolutionYamlFormat
import model.{ExerciseState, MyYamlProtocol, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.implicitConversions
import scala.util.Try

object ProgExYamlProtocol extends MyYamlProtocol {

  private val logger = Logger(ProgExYamlProtocol.getClass)

  val basePath: Path = Paths.get("conf", "resources", "programming")

  object ProgCollectionYamlFormat extends MyYamlObjectFormat[ProgCollection] {

    override protected def readObject(yamlObject: YamlObject): Try[ProgCollection] = for {
      id <- yamlObject.intField(idName)
      title <- yamlObject.stringField(titleName)
      author <- yamlObject.stringField(authorName)
      text <- yamlObject.stringField(textName)
      state <- yamlObject.enumField(statusName, ExerciseState.withNameInsensitiveOption) map (_ getOrElse ExerciseState.CREATED)
      shortName <- yamlObject.stringField(shortNameName)
    } yield ProgCollection(id, title, author, text, state, shortName)

    override def write(obj: ProgCollection): YamlValue = ???

  }

  object ProgExYamlFormat extends MyYamlObjectFormat[ProgExercise] {

    override def readObject(yamlObject: YamlObject): Try[ProgExercise] = for {
      baseValues <- readBaseValues(yamlObject)

      functionName <- yamlObject.stringField(functionNameName)
      foldername <- yamlObject.stringField(foldernameName)
      filename <- yamlObject.stringField(filenameName)

      inputTypes <- yamlObject.arrayField(inputTypesName, ProgInputTypeYamlFormat.read)
      outputType <- yamlObject.enumField(outputTypeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

      baseData <- yamlObject.optJsonField(baseDataName)

      unitTestPart <- yamlObject.objField(unitTestPartName, UnitTestPartYamlFormat.read)
      implementationPart <- yamlObject.objField(implementationPartName, ImplementationPartYamlFormat.read)

      sampleSolutions <- yamlObject.arrayField(sampleSolutionsName, ProgSampleSolutionYamlFormat.read)
      sampleTestDataTries <- yamlObject.arrayField(sampleTestDataName, ProgSampleTestdataYamlFormat.read)

      maybeClassDiagramPart <- yamlObject.optField(classDiagramName, UmlSampleSolutionYamlFormat.read).map(_.map(_.sample))
    } yield {
      for (sampleTdFailure <- sampleTestDataTries._2)
      // FIXME: return...
        logger.error("Could not read sample test data", sampleTdFailure.exception)

      for (inputTypeFailure <- inputTypes._2)
      // FIXME: return ...
        logger.error("Could not read input type name ", inputTypeFailure.exception)

      for (sampleSolutionFailure <- sampleSolutions._2)
      // FIXME: return ...
        logger.error("Could not read programming sample solution", sampleSolutionFailure.exception)

      ProgExercise(
        baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
        functionName, foldername, filename,
        inputTypes._1, outputType, baseData,
        unitTestPart, implementationPart,
        sampleSolutions._1, sampleTestDataTries._1, maybeClassDiagramPart
      )
    }

    override def write(exercise: ProgExercise): YamlObject = YamlObject(
      YamlString(idName) -> exercise.id,
      YamlString(titleName) -> exercise.title,
      YamlString(authorName) -> exercise.author,
      YamlString(textName) -> exercise.text,
      YamlString(statusName) -> exercise.state.entryName,
      YamlString(semanticVersionName) -> exercise.semanticVersion.asString,
      YamlString(functionNameName) -> YamlString(exercise.functionName),
      YamlString(inputTypesName) -> YamlArr(exercise.inputTypes.map(it => YamlString(it.inputType.typeName))),
      YamlString(sampleSolutionsName) -> YamlArr(exercise.sampleSolutions map ProgSampleSolutionYamlFormat.write),
      YamlString(sampleTestDataName) -> YamlArr(exercise.sampleTestData map ProgSampleTestdataYamlFormat.write)
    )

  }

  private object UnitTestPartYamlFormat extends MyYamlObjectFormat[UnitTestPart] {

    override protected def readObject(yamlObject: YamlObject): Try[UnitTestPart] = for {
      unitTestType <- yamlObject.enumField(unitTestTypeName, UnitTestTypes.withNameInsensitive)
      unitTestsDescription <- yamlObject.stringField(unitTestsDescriptionName)
      unitTestFiles <- yamlObject.arrayField(unitTestFilesName, ExerciseFileYamlFormat.read)
      unitTestTestConfigs <- yamlObject.arrayField(unitTestTestConfigsName, UnitTestTestConfigYamlFormat.read)
    } yield {
      for (unitTestTestConfigFailure <- unitTestTestConfigs._2)
        logger.error("Could not read unit test test config", unitTestTestConfigFailure.exception)

      for (unitTestFileFailure <- unitTestFiles._2)
        logger.error("Could not read unit test file", unitTestFileFailure.exception)

      UnitTestPart(unitTestType, unitTestsDescription, unitTestFiles._1, unitTestTestConfigs._1)
    }

    override def write(obj: UnitTestPart): YamlValue = ???

  }

  private object ImplementationPartYamlFormat extends MyYamlObjectFormat[ImplementationPart] {

    override protected def readObject(yamlObject: YamlObject): Try[ImplementationPart] = for {
      base <- yamlObject.stringField(baseName)
      files <- yamlObject.arrayField(filesName, ExerciseFileYamlFormat.read)
    } yield ImplementationPart(base, files._1)

    override def write(obj: ImplementationPart): YamlValue = ???

  }

  private object ProgInputTypeYamlFormat extends MyYamlObjectFormat[ProgInput] {

    override protected def readObject(yamlObject: YamlObject): Try[ProgInput] = for {
      id <- yamlObject.intField(idName)
      inputName <- yamlObject.stringField(nameName)
      inputType <- yamlObject.enumField(typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
    } yield ProgInput(id, inputName, inputType)

    override def write(obj: ProgInput): YamlValue = ???

  }

  case object ProgSampleSolutionYamlFormat extends MyYamlObjectFormat[ProgSampleSolution] {

    override def readObject(yamlObject: YamlObject): Try[ProgSampleSolution] = for {
      id <- yamlObject.intField(idName)
      files <- yamlObject.arrayField(filesName, ExerciseFileYamlFormat.read)
    } yield ProgSampleSolution(id, ProgSolution(files._1, testData = Seq.empty))

    override def write(pss: ProgSampleSolution): YamlValue = YamlObj(
      idName -> pss.id,
      filesName -> YamlArr(pss.sample.files.map(ExerciseFileYamlFormat.write)),
    )

  }


  private object UnitTestTestConfigYamlFormat extends MyYamlObjectFormat[UnitTestTestConfig] {

    override protected def readObject(yamlObject: YamlObject): Try[UnitTestTestConfig] = for {
      id <- yamlObject.intField(idName)
      shouldFail <- yamlObject.boolField("shouldFail")
      description <- yamlObject.stringField(descriptionName)
      cause <- yamlObject.optStringField("cause")
    } yield UnitTestTestConfig(id, shouldFail, cause, description)

    override def write(obj: UnitTestTestConfig): YamlValue = ???

  }

  private object ProgSampleTestdataYamlFormat extends MyYamlObjectFormat[ProgSampleTestData] {

    override def readObject(yamlObject: YamlObject): Try[ProgSampleTestData] = for {
      id <- yamlObject.intField(idName)
      inputAsJson <- yamlObject.jsonField(inputsName)
      output <- yamlObject.jsonField(outputName)
    } yield ProgSampleTestData(id, inputAsJson, output)

    override def write(cstd: ProgSampleTestData): YamlValue = YamlObj(
      idName -> cstd.id,
      outputName -> cstd.output.toString(),
      inputsName -> cstd.input.toString()
    )

  }

}
