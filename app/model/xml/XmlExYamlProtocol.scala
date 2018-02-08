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
      rootNode <- yamlObject.stringField(ROOT_NODE_NAME)
      refFileContent <- yamlObject.stringField(REF_FILE_CONTENT_NAME)
    } yield XmlExercise(baseValues, exerciseType, rootNode, refFileContent)

    override protected def writeRest(completeEx: XmlExercise): Map[YamlValue, YamlValue] = Map(
      YamlString(ExerciseTypeName) -> completeEx.exerciseType.name,
      YamlString(ROOT_NODE_NAME) -> completeEx.rootNode,
      YamlString(REF_FILE_CONTENT_NAME) -> completeEx.refFileContent
    )
  }

}