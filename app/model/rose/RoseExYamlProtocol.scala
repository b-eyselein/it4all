package model.rose

import model.MyYamlProtocol._
import model.programming.ProgConsts._
import model.programming.{ProgDataTypes, ProgLanguages}
import model.{BaseValues, MyYamlProtocol, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.util.Try

object RoseExYamlProtocol extends MyYamlProtocol {

  implicit object RoseExYamlFormat extends MyYamlObjectFormat[RoseCompleteEx] {

    override protected def readObject(yamlObject: YamlObject): Try[RoseCompleteEx] = for {
      baseValues <- readBaseValues(yamlObject)

      fieldWidth <- yamlObject.intField("fieldWidth")
      fieldHeight <- yamlObject.intField("fieldHeight")
      isMp <- yamlObject.boolField("isMultiplayer")
      inputTypes <- yamlObject.arrayField("inputTypes", RoseInputTypeYamlFormat(baseValues).read)
      sampleSolutions <- yamlObject.arrayField("sampleSolutions", RoseSampleSolutionYamlFormat(baseValues).read)
    } yield {
      for (inputTypeFailure <- inputTypes._2)
      // FIXME: return...
        Logger.error("Could not read rose input type", inputTypeFailure.exception)

      for (sampleSolFailure <- sampleSolutions._2)
      //FIXME: return...
        Logger.error("Could not read rose sample sol", sampleSolFailure.exception)

      RoseCompleteEx(RoseExercise(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
        fieldWidth, fieldHeight, isMp), inputTypes._1, sampleSolutions._1)
    }

    override def write(completeEx: RoseCompleteEx): YamlObject = ???

  }

  case class RoseInputTypeYamlFormat(baseValues: BaseValues) extends MyYamlObjectFormat[RoseInputType] {

    override def readObject(yamlObject: YamlObject): Try[RoseInputType] = for {
      id <- yamlObject.intField(idName)
      name <- yamlObject.stringField(nameName)
      inputType <- yamlObject.enumField(typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
    } yield RoseInputType(id, baseValues.id, baseValues.semanticVersion, name, inputType)

    override def write(obj: RoseInputType): YamlValue = ???

  }

  case class RoseSampleSolutionYamlFormat(baseValues: BaseValues) extends MyYamlObjectFormat[RoseSampleSolution] {

    override def readObject(yamlObject: YamlObject): Try[RoseSampleSolution] = for {
      language <- yamlObject.enumField(languageName, ProgLanguages.withNameInsensitiveOption) map (_ getOrElse ProgLanguages.STANDARD_LANG)
      sample <- yamlObject.stringField(sampleName)
    } yield RoseSampleSolution(baseValues.id, baseValues.semanticVersion, language, sample)

    override def write(pss: RoseSampleSolution): YamlValue = YamlObj(
      languageName -> pss.language.entryName,
      sampleName -> pss.solution
    )

  }

}
