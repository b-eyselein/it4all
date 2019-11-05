package model.tools.xml

import model.MyYamlProtocol
import model.MyYamlProtocol._
import model.tools.xml.XmlConsts._
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object XmlExYamlProtocol extends MyYamlProtocol {

  private val logger = Logger(XmlExYamlProtocol.getClass)

  object XmlExYamlFormat extends MyYamlObjectFormat[XmlExercise] {

    override def readObject(yamlObject: YamlObject): Try[XmlExercise] = for {
      baseValues <- readBaseValues(yamlObject)

      grammarDescription <- yamlObject.stringField(grammarDescriptionName)
      rootNode <- yamlObject.stringField(rootNodeName)

      samples <- yamlObject.arrayField(samplesName, XmlSampleYamlFormat.read)
    } yield {
      for (sampleReadError <- samples._2)
        logger.error("Could not read xml sample", sampleReadError.exception)

      XmlExercise(
        baseValues.id, baseValues.collId, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
        grammarDescription, rootNode, samples._1
      )
    }

    override def write(exercise: XmlExercise) = YamlObject(
      YamlString(idName) -> exercise.id,
      YamlString(titleName) -> exercise.title,
      YamlString(authorName) -> exercise.author,
      YamlString(textName) -> exercise.text,
      YamlString(statusName) -> exercise.state.entryName,
      YamlString(semanticVersionName) -> exercise.semanticVersion.asString,
      YamlString(grammarDescriptionName) -> exercise.grammarDescription,
      YamlString(samplesName) -> YamlArray(exercise.samples map XmlSampleYamlFormat.write toVector),
      YamlString(rootNodeName) -> exercise.rootNode
    )

  }

  object XmlSampleYamlFormat extends MyYamlObjectFormat[XmlSampleSolution] {

    override protected def readObject(yamlObject: YamlObject): Try[XmlSampleSolution] = for {
      id <- yamlObject.intField(idName)
      sampleGrammar <- yamlObject.stringField(grammarName)
      sampleDocument <- yamlObject.stringField(documentName)
    } yield XmlSampleSolution(id, XmlSolution(sampleDocument, sampleGrammar))

    override def write(obj: XmlSampleSolution): YamlValue = YamlObject(
      YamlString(idName) -> obj.id,
      YamlString(grammarName) -> obj.sample.grammar,
      YamlString(documentName) -> obj.sample.document
    )

  }


}
