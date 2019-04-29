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
      folderIdentifier <- yamlObject.stringField(identifierName)

      functionName <- yamlObject.stringField(functionNameName)
      outputType <- yamlObject.enumField(outputTypeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
      baseData <- yamlObject.optJsonField(baseDataName)

      inputTypes <- yamlObject.arrayField(inputTypesName, ProgInputTypeYamlFormat.read)
      sampleSolutions <- yamlObject.arrayField(sampleSolutionsName, ProgSampleSolutionYamlFormat(folderIdentifier).read)
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
        folderIdentifier, functionName, outputType, baseData,
        inputTypes._1, sampleSolutions._1, sampleTestDataTries._1, maybeClassDiagramPart
      )
    }

    override def write(exercise: ProgExercise): YamlObject = {
      val progSampleSolYamlFormat = ProgSampleSolutionYamlFormat(exercise.folderIdentifier)
      YamlObject(
        writeBaseValues(exercise.baseValues) ++
          Map[YamlValue, YamlValue](
            YamlString(functionNameName) -> YamlString(exercise.functionName),
            YamlString(inputTypesName) -> YamlArr(exercise.inputTypes.map(it => YamlString(it.inputType.typeName))),
            YamlString(sampleSolutionsName) -> YamlArr(exercise.sampleSolutions map progSampleSolYamlFormat.write),
            YamlString(sampleTestDataName) -> YamlArr(exercise.sampleTestData map ProgSampleTestdataYamlFormat.write)
          )
      )
    }

  }

  private object ProgInputTypeYamlFormat extends MyYamlObjectFormat[ProgInput] {

    override protected def readObject(yamlObject: YamlObject): Try[ProgInput] = for {
      id <- yamlObject.intField(idName)
      inputName <- yamlObject.stringField(nameName)
      inputType <- yamlObject.enumField(typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
    } yield ProgInput(id, inputName, inputType)

    override def write(obj: ProgInput): YamlValue = ???

  }

  final case class ProgSampleSolutionYamlFormat(folderIdentifier: String) extends MyYamlObjectFormat[ProgSampleSolution] {

    override def readObject(yamlObject: YamlObject): Try[ProgSampleSolution] = for {
      id <- yamlObject.intField(idName)
      //      language <- yamlObject.enumField(languageName, ProgLanguages.withNameInsensitiveOption) map (_ getOrElse ProgLanguages.PYTHON_3)
      base <- yamlObject.stringField(baseName)
      sample <- yamlObject.stringField(sampleName)
    } yield ProgSampleSolution(id, /* language,*/ base, sample)

    override def write(pss: ProgSampleSolution): YamlValue = YamlObj(
      languageName -> pss.language.entryName,
      sampleName -> pss.sample.implementation
    )

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
      inputsName -> cstd.inputAsJson.toString()
    )

  }

}
