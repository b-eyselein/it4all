package model.spread

import model.MyYamlProtocol._
import model.spread.SpreadConsts._
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

object SpreadExYamlProtocol extends MyYamlProtocol {

  implicit object SpreadExYamlFormat extends ExYamlFormat[SpreadExercise] {

    override def write(spreadCompEx: SpreadExercise): YamlValue = ???

    override def readRest(yamlObject: YamlObject, baseValues: BaseValues): SpreadExercise =
      SpreadExercise(baseValues, yamlObject.stringField(SAMPLE_FILENAME), yamlObject.stringField(TEMPALTE_FILENAME))

  }

}
