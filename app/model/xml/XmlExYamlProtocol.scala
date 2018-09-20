package model.xml

import model.MyYamlProtocol._
import model.xml.XmlConsts._
import model.xml.dtd.DocTypeDefParser
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object XmlExYamlProtocol extends MyYamlProtocol {

  implicit object XmlExYamlFormat extends MyYamlObjectFormat[XmlCompleteExercise] {

    override def readObject(yamlObject: YamlObject): Try[XmlCompleteExercise] = for {
      baseValues <- readBaseValues(yamlObject)

      grammarDescription <- yamlObject.stringField(grammarDescriptionName)
      rootNode <- yamlObject.stringField(rootNodeName)

      sampleGrammars <- yamlObject.arrayField("sampleGrammars", XmlSampleGrammarYamlFormat(baseValues).read)
      sampleDocuments <- yamlObject.arrayField("sampleDocuments", XmlSampleDocumentYamlFormat(baseValues).read)
    } yield {
      for (grammarReadError <- sampleGrammars._2)
        Logger.error("Could not read xml sample grammar", grammarReadError.exception)

      for (documentReadError <- sampleDocuments._2)
        Logger.error("Could not read xml sample document", documentReadError.exception)

      XmlCompleteExercise(
        XmlExercise(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state, grammarDescription, rootNode),
        sampleGrammars._1, sampleDocuments._1)
    }

    override def write(completeEx: XmlCompleteExercise) = new YamlObject(
      writeBaseValues(completeEx.ex) ++
        Map[YamlValue, YamlValue](
          YamlString(grammarDescriptionName) -> YamlString(completeEx.ex.grammarDescription),
          YamlString(sampleGrammarsName) -> YamlArray(completeEx.sampleGrammars map XmlSampleGrammarYamlFormat(completeEx.ex.baseValues).write toVector),
          YamlString(rootNodeName) -> YamlString(completeEx.ex.rootNode)
        )
    )
  }

  final case class XmlSampleGrammarYamlFormat(baseValues: BaseValues) extends MyYamlObjectFormat[XmlSampleGrammar] {

    override protected def readObject(yamlObject: YamlObject): Try[XmlSampleGrammar] = for {
      id <- yamlObject.intField(idName)
      sampleGrammar <- yamlObject.stringField(grammarName) flatMap DocTypeDefParser.tryParseDTD
    } yield XmlSampleGrammar(id, baseValues.id, baseValues.semanticVersion, sampleGrammar)

    override def write(obj: XmlSampleGrammar): YamlValue = YamlObject(
      YamlString(idName) -> YamlNumber(obj.id),
      YamlString(grammarName) -> YamlString(obj.sampleGrammar.asString)
    )

  }


  final case class XmlSampleDocumentYamlFormat(values: BaseValues) extends MyYamlObjectFormat[XmlSampleDocument] {

    override protected def readObject(yamlObject: YamlObject): Try[XmlSampleDocument] = for {
      id <- yamlObject.intField(idName)
      sampleDocument <- yamlObject.stringField(documentName)
    } yield XmlSampleDocument(id, values.id, values.semanticVersion, sampleDocument)

    override def write(obj: XmlSampleDocument): YamlValue = ???

  }

}