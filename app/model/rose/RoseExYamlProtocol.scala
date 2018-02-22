package model.rose

import model.MyYamlProtocol._
import model.programming.ProgConsts._
import model.programming.{ProgDataTypes, ProgLanguage}
import model.{BaseValues, MyYamlProtocol, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.util.Try

object RoseExYamlProtocol extends MyYamlProtocol {

  implicit object RoseExYamlFormat extends HasBaseValuesYamlFormat[RoseCompleteEx] {


    protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): Try[RoseCompleteEx] = for {
      isMp <- yamlObject.boolField("isMultiplayer")
      inputTypes <- yamlObject.arrayField("inputTypes", RoseInputTypeYamlFormat(baseValues.id).read)
      sampleSolution <- yamlObject.someField("sampleSolution") flatMap RoseSampleSolutionYamlFormat(baseValues.id).read
    } yield {
      for (inputTypeFailure <- inputTypes._2)
      // FIXME: return...
        Logger.error("Could not read rose input type", inputTypeFailure.exception)

      RoseCompleteEx(RoseExercise(baseValues, isMp), inputTypes._1, sampleSolution)
    }

    protected def writeRest(completeEx: RoseCompleteEx): Map[YamlValue, YamlValue] = ???

  }

  case class RoseInputTypeYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[RoseInputType] {

    override def readObject(yamlObject: YamlObject): Try[RoseInputType] = for {
      id <- yamlObject.intField(ID_NAME)
      name <- yamlObject.stringField("name")
      inputType <- yamlObject.enumField("type", str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
    } yield RoseInputType(id, exerciseId, name, inputType)

    override def write(obj: RoseInputType): YamlValue = ???

  }

  case class RoseSampleSolutionYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[RoseSampleSolution] {

    override def readObject(yamlObject: YamlObject): Try[RoseSampleSolution] = for {
      language <- yamlObject.enumField(LANGUAGE_NAME, ProgLanguage.valueOf(_).get)
      sample <- yamlObject.stringField(SAMPLE_NAME)
    } yield RoseSampleSolution(exerciseId, language, sample)

    override def write(pss: RoseSampleSolution): YamlValue = YamlObj(
      LANGUAGE_NAME -> pss.language.name,
      SAMPLE_NAME -> pss.solution
    )

  }

}
