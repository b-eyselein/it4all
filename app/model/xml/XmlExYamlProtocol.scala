package model.xml

import model.MyYamlProtocol._
import model.xml.XmlConsts._
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object XmlExYamlProtocol extends MyYamlProtocol {

  implicit object XmlExYamlFormat extends HasBaseValuesYamlFormat[XmlExercise] {

    override def readRest(yamlObject: YamlObject, baseValues: BaseValues): Try[XmlExercise] = for {
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