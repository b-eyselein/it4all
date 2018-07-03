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
    } yield SpreadExercise(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state, sampleFilename, templateFilename)

    override def write(completeEx: SpreadExercise) = YamlObject(
      writeBaseValues(completeEx.ex) ++ Map(
        YamlString(SAMPLE_FILENAME) -> YamlString(completeEx.sampleFilename),
        YamlString(TEMPALTE_FILENAME) -> YamlString(completeEx.templateFilename)
      )
    )

  }

}
