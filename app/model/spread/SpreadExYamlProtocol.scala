package model.spread

import model.Enums.ExerciseState
import model.MyYamlProtocol
import model.MyYamlProtocol._
import model.spread.SpreadConsts._
import net.jcazevedo.moultingyaml._

import scala.util.Try

object SpreadExYamlProtocol extends MyYamlProtocol {

  implicit object SpreadExYamlFormat extends HasBaseValuesYamlFormat[SpreadExercise] {

    override def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[SpreadExercise] = for {
      sampleFilename <- yamlObject.stringField(SAMPLE_FILENAME)
      templateFilename <- yamlObject.stringField(TEMPALTE_FILENAME)
    } yield new SpreadExercise(baseValues, sampleFilename, templateFilename)

    override protected def writeRest(completeEx: SpreadExercise): Map[YamlValue, YamlValue] = Map(
      YamlString(SAMPLE_FILENAME) -> completeEx.sampleFilename,
      YamlString(TEMPALTE_FILENAME) -> completeEx.templateFilename
    )
  }

}
