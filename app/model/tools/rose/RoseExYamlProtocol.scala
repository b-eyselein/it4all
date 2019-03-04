package model.tools.rose

import model.MyYamlProtocol._
import model.tools.programming.ProgConsts._
import model.tools.programming.{ProgDataTypes, ProgLanguages}
import model.{ExerciseState, MyYamlProtocol, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.util.Try

object RoseExYamlProtocol extends MyYamlProtocol {

  private val logger = Logger(RoseExYamlProtocol.getClass)

  object RoseCollectionYamlFormat extends MyYamlObjectFormat[RoseCollection] {

    override protected def readObject(yamlObject: YamlObject): Try[RoseCollection] = for {
      id <- yamlObject.intField(idName)
      title <- yamlObject.stringField(titleName)
      author <- yamlObject.stringField(authorName)
      text <- yamlObject.stringField(textName)
      state <- yamlObject.enumField(statusName, ExerciseState.withNameInsensitiveOption) map (_ getOrElse ExerciseState.CREATED)
      shortName <- yamlObject.stringField(shortNameName)
    } yield RoseCollection(id, title, author, text, state, shortName)

    override def write(obj: RoseCollection): YamlValue = ???

  }

  object RoseExYamlFormat extends MyYamlObjectFormat[RoseExercise] {

    override protected def readObject(yamlObject: YamlObject): Try[RoseExercise] = for {
      baseValues <- readBaseValues(yamlObject)

      fieldWidth <- yamlObject.intField("fieldWidth")
      fieldHeight <- yamlObject.intField("fieldHeight")
      isMp <- yamlObject.boolField("isMultiplayer")
      inputTypes <- yamlObject.arrayField("inputTypes", RoseInputTypeYamlFormat.read)
      sampleSolutions <- yamlObject.arrayField("sampleSolutions", RoseSampleSolutionYamlFormat.read)
    } yield {
      for (inputTypeFailure <- inputTypes._2)
      // FIXME: return...
        logger.error("Could not read rose input type", inputTypeFailure.exception)

      for (sampleSolFailure <- sampleSolutions._2)
      //FIXME: return...
        logger.error("Could not read rose sample sol", sampleSolFailure.exception)

      RoseExercise(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
        fieldWidth, fieldHeight, isMp, inputTypes._1, sampleSolutions._1)
    }

    override def write(exercise: RoseExercise): YamlObject = ???

  }

  object RoseInputTypeYamlFormat extends MyYamlObjectFormat[RoseInputType] {

    override def readObject(yamlObject: YamlObject): Try[RoseInputType] = for {
      id <- yamlObject.intField(idName)
      name <- yamlObject.stringField(nameName)
      inputType <- yamlObject.enumField(typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
    } yield RoseInputType(id, name, inputType)

    override def write(obj: RoseInputType): YamlValue = ???

  }

  object RoseSampleSolutionYamlFormat extends MyYamlObjectFormat[RoseSampleSolution] {

    override def readObject(yamlObject: YamlObject): Try[RoseSampleSolution] = for {
      id <- yamlObject.intField(idName)
      language <- yamlObject.enumField(languageName, ProgLanguages.withNameInsensitiveOption) map (_ getOrElse ProgLanguages.StandardLanguage)
      sample <- yamlObject.stringField(sampleName)
    } yield RoseSampleSolution(id, language, sample)

    override def write(obj: RoseSampleSolution): YamlValue = YamlObj(
      idName -> obj.id,
      languageName -> obj.language.entryName,
      sampleName -> obj.sample
    )

  }

}
