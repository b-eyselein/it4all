package model.xml

import model.MyYamlProtocol._
import model.xml.XmlConsts._
import model.xml.dtd.DocTypeDefParser
import model.{MyYamlProtocol, SemanticVersion}
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

      samples <- yamlObject.arrayField(samplesName, XmlSampleYamlFormat(baseValues.id, baseValues.semanticVersion).read)
    } yield {
      for (sampleReadError <- samples._2)
        Logger.error("Could not read xml sample", sampleReadError.exception)

      XmlCompleteExercise(
        XmlExercise(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state, grammarDescription, rootNode),
        samples._1)
    }

    override def write(completeEx: XmlCompleteExercise) = new YamlObject(
      writeBaseValues(completeEx.ex) ++
        Map[YamlValue, YamlValue](
          YamlString(grammarDescriptionName) -> YamlString(completeEx.ex.grammarDescription),
          YamlString(samplesName) -> YamlArray(completeEx.sampleGrammars map XmlSampleYamlFormat(completeEx.ex.id, completeEx.ex.semanticVersion).write toVector),
          YamlString(rootNodeName) -> YamlString(completeEx.ex.rootNode)
        )
    )
  }

  final case class XmlSampleYamlFormat(exerciseId: Int, semanticVersion: SemanticVersion) extends MyYamlObjectFormat[XmlSample] {

    override protected def readObject(yamlObject: YamlObject): Try[XmlSample] = for {
      id <- yamlObject.intField(idName)
      sampleGrammar <- yamlObject.stringField(grammarName) flatMap DocTypeDefParser.tryParseDTD
      sampleDocument <- yamlObject.stringField(documentName)
    } yield XmlSample(id, exerciseId, semanticVersion, sampleGrammar, sampleDocument)

    override def write(obj: XmlSample): YamlValue = YamlObject(
      YamlString(idName) -> YamlNumber(obj.id),
      YamlString(grammarName) -> YamlString(obj.sampleGrammar.asString),
      YamlString(documentName) -> YamlString(obj.sampleDocument)
    )

  }


}