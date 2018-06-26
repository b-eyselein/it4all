package model.xml

import model.MyYamlProtocol
import model.MyYamlProtocol._
import model.xml.XmlConsts._
import model.xml.dtd.DocTypeDefParser
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object XmlExYamlProtocol extends MyYamlProtocol {

  implicit object XmlExYamlFormat extends MyYamlObjectFormat[XmlCompleteExercise] {

    override def readObject(yamlObject: YamlObject): Try[XmlCompleteExercise] = for {
      baseValues <- readBaseValues(yamlObject)

      grammarDescription <- yamlObject.stringField(grammarDescriptionName)
      rootNode <- yamlObject.stringField(RootNodeName)

      sampleGrammars <- yamlObject.arrayField("sampleGrammars", XmlSampleGrammarYamlFormat(baseValues._1).read)
    } yield {
      for (grammarReadError <- sampleGrammars._2)
        Logger.error("Could not read xml sample grammar", grammarReadError.exception)

      XmlCompleteExercise(
        XmlExercise(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, grammarDescription, rootNode),
        sampleGrammars._1)
    }

    override def write(completeEx: XmlCompleteExercise) = new YamlObject(
      writeBaseValues(completeEx.ex) ++
        Map(
          YamlString(grammarDescriptionName) -> YamlString(completeEx.ex.grammarDescription),
          YamlString(sampleGrammarsName) -> YamlArray(completeEx.sampleGrammars map XmlSampleGrammarYamlFormat(completeEx.ex.id).write toVector),
          YamlString(RootNodeName) -> YamlString(completeEx.ex.rootNode)
        )
    )
  }

  case class XmlSampleGrammarYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[XmlSampleGrammar] {

    override protected def readObject(yamlObject: YamlObject): Try[XmlSampleGrammar] = for {
      id <- yamlObject.intField(idName)
      sampleGrammar <- yamlObject.stringField(grammarName) flatMap DocTypeDefParser.parseDTD
    } yield XmlSampleGrammar(id, exerciseId, sampleGrammar)

    override def write(obj: XmlSampleGrammar): YamlValue = YamlObject(
      YamlString(idName) -> YamlNumber(obj.id),
      YamlString(grammarName) -> YamlString(obj.sampleGrammar.asString)
    )

  }

}