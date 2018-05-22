package model.xml

import model.MyYamlProtocol._
import model.xml.XmlConsts._
import model.xml.dtd.DocTypeDefParser
import model.{ExerciseState, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object XmlExYamlProtocol extends MyYamlProtocol {

  implicit object XmlExYamlFormat extends HasBaseValuesYamlFormat[XmlExercise] {

    override def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[XmlExercise] = for {
      grammarDescription <- yamlObject.stringField(GrammarDescriptionName)
      sampleGrammar <- yamlObject.stringField(SampleGrammarName) flatMap DocTypeDefParser.parseDTD
      rootNode <- yamlObject.stringField(RootNodeName)
    } yield XmlExercise(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, grammarDescription, sampleGrammar, rootNode)

    override protected def writeRest(completeEx: XmlExercise): Map[YamlValue, YamlValue] = Map(
      YamlString(GrammarDescriptionName) -> completeEx.grammarDescription,
      YamlString(SampleGrammarName) -> completeEx.sampleGrammar.asString,
      YamlString(RootNodeName) -> completeEx.rootNode
    )
  }

}