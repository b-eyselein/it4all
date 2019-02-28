package model.rose

import model.MyYamlProtocol._
import model.programming.ProgConsts._
import model.programming.{ProgDataTypes, ProgLanguages}
import model.{MyYamlProtocol, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.util.Try

object RoseExYamlProtocol extends MyYamlProtocol {

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
        Logger.error("Could not read rose input type", inputTypeFailure.exception)

      for (sampleSolFailure <- sampleSolutions._2)
      //FIXME: return...
        Logger.error("Could not read rose sample sol", sampleSolFailure.exception)

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
