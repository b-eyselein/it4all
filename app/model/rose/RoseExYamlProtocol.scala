package model.rose

import model.MyYamlProtocol._
import model.programming.ProgConsts._
import model.programming.{ProgDataTypes, ProgLanguage}
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

object RoseExYamlProtocol extends MyYamlProtocol {

  implicit object RoseExYamlFormat extends HasBaseValuesYamlFormat[RoseCompleteEx] {


    protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): RoseCompleteEx = RoseCompleteEx(
      RoseExercise(baseValues, yamlObject.boolField("isMultiplayer")),
      yamlObject.arrayField("inputTypes", _ convertTo[RoseInputType] RoseInputTypeYamlFormat(baseValues.id)),
      yamlObject.objectField("sampleSolution", RoseSampleSolutionYamlFormat(baseValues.id))
    )

    protected def writeRest(completeEx: RoseCompleteEx): Map[YamlValue, YamlValue] = ???

  }

  case class RoseInputTypeYamlFormat(exerciseId: Int) extends MyYamlFormat[RoseInputType] {

    override def readObject(yamlObject: YamlObject): RoseInputType =
      RoseInputType(yamlObject.intField(ID_NAME), exerciseId,
        yamlObject.stringField("name"),
        yamlObject.enumField("type", str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING, ProgDataTypes.STRING)
      )

    override def write(obj: RoseInputType): YamlValue = ???

  }

  case class RoseSampleSolutionYamlFormat(exerciseId: Int) extends MyYamlFormat[RoseSampleSolution] {

    override def readObject(yamlObject: YamlObject): RoseSampleSolution = RoseSampleSolution(
      exerciseId, yamlObject.enumField(LANGUAGE_NAME, ProgLanguage.valueOf(_).get, ProgLanguage.STANDARD_LANG), yamlObject.stringField(SAMPLE_NAME)
    )

    override def write(pss: RoseSampleSolution): YamlValue = YamlObject(
      YamlString(LANGUAGE_NAME) -> pss.language.name,
      YamlString(SAMPLE_NAME) -> pss.solution
    )

  }

}
