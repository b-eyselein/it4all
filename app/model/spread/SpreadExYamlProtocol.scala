package model.spread

import model.MyYamlProtocol
import model.MyYamlProtocol._
import model.spread.SpreadConsts._
import net.jcazevedo.moultingyaml._

import scala.util.Try

object SpreadExYamlProtocol extends MyYamlProtocol {

  implicit object SpreadExYamlFormat extends MyYamlObjectFormat[SpreadExercise] {

    override def readObject(yamlObject: YamlObject): Try[SpreadExercise] = for {
      baseValues <- readBaseValues(yamlObject)
      sampleFilename <- yamlObject.stringField(SAMPLE_FILENAME)
      templateFilename <- yamlObject.stringField(TEMPALTE_FILENAME)
    } yield SpreadExercise(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, baseValues._6, sampleFilename, templateFilename)

    override def write(completeEx: SpreadExercise) = YamlObject(
      writeBaseValues(completeEx.ex) ++ Map(
        YamlString(SAMPLE_FILENAME) -> YamlString(completeEx.sampleFilename),
        YamlString(TEMPALTE_FILENAME) -> YamlString(completeEx.templateFilename)
      )
    )

  }

}
