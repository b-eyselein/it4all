package model.xml

import model.ExerciseState
import model.MyYamlProtocol
import model.MyYamlProtocol._
import model.xml.XmlConsts._
import net.jcazevedo.moultingyaml._

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object XmlExYamlProtocol extends MyYamlProtocol {

  implicit object XmlExYamlFormat extends HasBaseValuesYamlFormat[XmlExercise] {

    override def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[XmlExercise] = for {
      grammarDescription <- yamlObject.stringField(GrammarDescriptionName)
      sampleGrammar <- yamlObject.stringField(SampleGrammarName)
      rootNode <- yamlObject.stringField(RootNodeName)
    } yield new XmlExercise(baseValues, grammarDescription, sampleGrammar, rootNode)

    override protected def writeRest(completeEx: XmlExercise): Map[YamlValue, YamlValue] = Map(
      YamlString(GrammarDescriptionName) -> completeEx.grammarDescription,
      YamlString(SampleGrammarName) -> completeEx.sampleGrammar,
      YamlString(RootNodeName) -> completeEx.rootNode
    )
  }

}