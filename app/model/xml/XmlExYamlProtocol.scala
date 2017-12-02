package model.xml

import model.MyYamlProtocol._
import model.xml.XmlConsts._
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

import scala.language.{implicitConversions, postfixOps}

object XmlExYamlProtocol extends MyYamlProtocol {

  implicit object XmlExYamlFormat extends HasBaseValuesYamlFormat[XmlExercise] {

    override def readRest(yamlObject: YamlObject, baseValues: BaseValues): XmlExercise = XmlExercise(baseValues,
      yamlObject.enumField(ExerciseTypeName, XmlExType.valueOf, XmlExType.XML_DTD),
      yamlObject.stringField(ROOT_NODE_NAME),
      yamlObject.stringField(REF_FILE_CONTENT_NAME))

    override protected def writeRest(completeEx: XmlExercise): Map[YamlValue, YamlValue] = Map(
      YamlString(ExerciseTypeName) -> completeEx.exerciseType.name,
      YamlString(ROOT_NODE_NAME) -> completeEx.rootNode,
      YamlString(REF_FILE_CONTENT_NAME) -> completeEx.refFileContent
    )
  }

}