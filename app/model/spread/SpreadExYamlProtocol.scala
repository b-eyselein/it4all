package model.spread

import model.MyYamlProtocol._
import model.spread.SpreadConsts._
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

object SpreadExYamlProtocol extends MyYamlProtocol {

  implicit object SpreadExYamlFormat extends ExYamlFormat[SpreadExercise] {

    override def readRest(yamlObject: YamlObject, baseValues: BaseValues): SpreadExercise =
      SpreadExercise(baseValues, yamlObject.stringField(SAMPLE_FILENAME), yamlObject.stringField(TEMPALTE_FILENAME))

    override protected def writeRest(completeEx: SpreadExercise): Map[YamlValue, YamlValue] = Map(
      YamlString(SAMPLE_FILENAME) -> completeEx.sampleFileName,
      YamlString(TEMPALTE_FILENAME) -> completeEx.templateFilename
    )
  }

}
