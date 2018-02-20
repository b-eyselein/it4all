package model.xml

import model.MyYamlProtocol._
import model.xml.XmlConsts._
import model.xml.XmlEnums.XmlExType
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object XmlExYamlProtocol extends MyYamlProtocol {

  implicit object XmlExYamlFormat extends HasBaseValuesYamlFormat[XmlExercise] {

    override def readRest(yamlObject: YamlObject, baseValues: BaseValues): Try[XmlExercise] = for {
      exerciseType <- yamlObject.enumField(ExerciseTypeName, XmlExType.valueOf)
      grammarDescription <- yamlObject.stringField(GrammarDescriptionName)
      rootNode <- yamlObject.stringField(RootNodeName)
      refFileContent <- yamlObject.stringField(RefFileContentName)
    } yield XmlExercise(baseValues, exerciseType, grammarDescription, rootNode, refFileContent)

    override protected def writeRest(completeEx: XmlExercise): Map[YamlValue, YamlValue] = Map(
      YamlString(ExerciseTypeName) -> completeEx.exerciseType.name,
      YamlString(GrammarDescriptionName) -> completeEx.grammarDescription,
      YamlString(RootNodeName) -> completeEx.rootNode,
      YamlString(RefFileContentName) -> completeEx.refFileContent
    )
  }

}