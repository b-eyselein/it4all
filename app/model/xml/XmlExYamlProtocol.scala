package model.xml

import model.MyYamlProtocol._
import model.xml.XmlConsts._
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

import scala.language.{implicitConversions, postfixOps}

object XmlExYamlProtocol extends MyYamlProtocol {

  implicit object XmlExYamlFormat extends ExYamlFormat[XmlExercise] {

    override def write(ex: XmlExercise): YamlValue = YamlObject(
      YamlString(ID_NAME) -> ex.id,
      YamlString(TITLE_NAME) -> ex.title,
      YamlString(AUTHOR_NAME) -> ex.author,
      YamlString(TEXT_NAME) -> ex.text,
      YamlString(STATE_NAME) -> ex.state.name,

      // Exercise specific values
      YamlString(EXERCISE_TYPE) -> ex.exerciseType.name,
      YamlString(ROOT_NODE_NAME) -> ex.rootNode,
      YamlString(REF_FILE_CONTENT_NAME) -> ex.refFileContent
    )


    override def readRest(yamlObject: YamlObject, baseValues: BaseValues): XmlExercise = XmlExercise(baseValues,
      yamlObject.enumField(EXERCISE_TYPE, XmlExType.valueOf, XmlExType.XML_DTD),
      yamlObject.stringField(ROOT_NODE_NAME),
      yamlObject.stringField(REF_FILE_CONTENT_NAME))

  }

}