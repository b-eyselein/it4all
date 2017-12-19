package model.ebnf

import model.MyYamlProtocol._
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml.{YamlObject, YamlValue}

import scala.language.postfixOps

object EbnfExerciseYamlProtocol extends MyYamlProtocol {

  implicit object EbnfExerciseYamlFormat extends HasBaseValuesYamlFormat[EbnfCompleteExercise] {

    override protected def readRest(yamlObject: YamlObject, baseValues: BaseValues) = EbnfCompleteExercise(
      EbnfExercise(baseValues, yamlObject.arrayField("terminals", _.forgivingStr) flatten),
      testdata = yamlObject.arrayField("testdata", _.asStr map (str => EbnfTestData(baseValues.id, str))) flatten
    )

    override protected def writeRest(completeEx: EbnfCompleteExercise): Map[YamlValue, YamlValue] = ???

  }

}
