package model.tools.xml

import model.MyYamlProtocol._
import model.tools.sql.SqlConsts.{authorName, idName, shortNameName, statusName, textName, titleName}
import model.tools.xml.XmlConsts._
import model.{ExerciseState, MyYamlProtocol}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object XmlExYamlProtocol extends MyYamlProtocol {

  private val logger = Logger(XmlExYamlProtocol.getClass)

  object XmlCollectionYamlFormat extends MyYamlObjectFormat[XmlCollection] {

    override protected def readObject(yamlObject: YamlObject): Try[XmlCollection] = for {
      id <- yamlObject.intField(idName)
      title <- yamlObject.stringField(titleName)
      author <- yamlObject.stringField(authorName)
      text <- yamlObject.stringField(textName)
      state <- yamlObject.enumField(statusName, ExerciseState.withNameInsensitiveOption) map (_ getOrElse ExerciseState.CREATED)
      shortName <- yamlObject.stringField(shortNameName)
    } yield XmlCollection(id, title, author, text, state, shortName)

    override def write(obj: XmlCollection): YamlValue = ???

  }

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
        baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
        grammarDescription, rootNode, samples._1)
    }

    override def write(exercise: XmlExercise) = new YamlObject(
      writeBaseValues(exercise.baseValues) ++
        Map[YamlValue, YamlValue](
          YamlString(grammarDescriptionName) -> YamlString(exercise.grammarDescription),
          YamlString(samplesName) -> YamlArray(exercise.samples map XmlSampleYamlFormat.write toVector),
          YamlString(rootNodeName) -> YamlString(exercise.rootNode)
        )
    )
  }

  private object XmlSampleYamlFormat extends MyYamlObjectFormat[XmlSampleSolution] {

    override protected def readObject(yamlObject: YamlObject): Try[XmlSampleSolution] = for {
      id <- yamlObject.intField(idName)
      sampleGrammar <- yamlObject.stringField(grammarName)
      sampleDocument <- yamlObject.stringField(documentName)
    } yield XmlSampleSolution(id, XmlSolution(sampleDocument, sampleGrammar))

    override def write(obj: XmlSampleSolution): YamlValue = YamlObject(
      YamlString(idName) -> YamlNumber(obj.id),
      YamlString(grammarName) -> YamlString(obj.sample.grammar),
      YamlString(documentName) -> YamlString(obj.sample.document)
    )

  }


}
