package model.rose

import model.Enums.ExerciseState
import model.MyYamlProtocol._
import model.programming.ProgConsts._
import model.programming.{ProgDataTypes, ProgLanguage}
import model.{MyYamlProtocol, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.util.Try

object RoseExYamlProtocol extends MyYamlProtocol {

  implicit object RoseExYamlFormat extends HasBaseValuesYamlFormat[RoseCompleteEx] {

    protected def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[RoseCompleteEx] = for {
      fieldWidth <- yamlObject.intField("fieldWidth")
      fieldHeight <- yamlObject.intField("fieldHeight")
      isMp <- yamlObject.boolField("isMultiplayer")
      inputTypes <- yamlObject.arrayField("inputTypes", RoseInputTypeYamlFormat(baseValues._1).read)
      sampleSolutions <- yamlObject.arrayField("sampleSolutions", RoseSampleSolutionYamlFormat(baseValues._1).read)
    } yield {
      for (inputTypeFailure <- inputTypes._2)
      // FIXME: return...
        Logger.error("Could not read rose input type", inputTypeFailure.exception)

      for (sampleSolFailure <- sampleSolutions._2)
      //FIXME: return...
        Logger.error("Could not read rose sample sol", sampleSolFailure.exception)

      RoseCompleteEx(new RoseExercise(baseValues, fieldWidth, fieldHeight, isMp), inputTypes._1, sampleSolutions._1)
    }

    protected def writeRest(completeEx: RoseCompleteEx): Map[YamlValue, YamlValue] = ???

  }

  case class RoseInputTypeYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[RoseInputType] {

    override def readObject(yamlObject: YamlObject): Try[RoseInputType] = for {
      id <- yamlObject.intField(idName)
      name <- yamlObject.stringField("name")
      inputType <- yamlObject.enumField("type", str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)
    } yield RoseInputType(id, exerciseId, name, inputType)

    override def write(obj: RoseInputType): YamlValue = ???

  }

  case class RoseSampleSolutionYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[RoseSampleSolution] {

    override def readObject(yamlObject: YamlObject): Try[RoseSampleSolution] = for {
      language <- yamlObject.enumField(LanguageName, ProgLanguage.valueOf(_).get)
      sample <- yamlObject.stringField(SAMPLE_NAME)
    } yield RoseSampleSolution(exerciseId, language, sample)

    override def write(pss: RoseSampleSolution): YamlValue = YamlObj(
      LanguageName -> pss.language.name,
      SAMPLE_NAME -> pss.solution
    )

  }

}
