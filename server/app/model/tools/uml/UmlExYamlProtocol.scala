package model.tools.uml

import model.{ExerciseState, MyYamlProtocol, SemanticVersion}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}

object UmlExYamlProtocol extends MyYamlProtocol {

  private val umlClassYamlFormat: YamlFormat[UmlClass] = {
    implicit val uctyf: YamlFormat[UmlClassType] = new EnumYamlFormat(UmlClassType)

    implicit val uvyf: YamlFormat[UmlVisibility] = new EnumYamlFormat(UmlVisibility)

    implicit val uayf: YamlFormat[UmlAttribute] = yamlFormat6(UmlAttribute)

    implicit val umyf: YamlFormat[UmlMethod] = yamlFormat6(UmlMethod)

    implicit val upyf: YamlFormat[MyPosition] = yamlFormat2(MyPosition)

    yamlFormat5(UmlClass)
  }

  private val umlAssociationYamlFormat: YamlFormat[UmlAssociation] = {
    implicit val uatyf: YamlFormat[UmlAssociationType] = new EnumYamlFormat(UmlAssociationType)

    implicit val umyf: YamlFormat[UmlMultiplicity] = new EnumYamlFormat[UmlMultiplicity](UmlMultiplicity)

    yamlFormat6(UmlAssociation)
  }

  private val umlSampleSolutionYamlFormat = {
    implicit val ucdyf: YamlFormat[UmlClassDiagram] = {
      implicit val ucyf: YamlFormat[UmlClass] = umlClassYamlFormat

      implicit val uayf: YamlFormat[UmlAssociation] = umlAssociationYamlFormat

      implicit val uiyf: YamlFormat[UmlImplementation] = yamlFormat2(UmlImplementation)

      yamlFormat3(UmlClassDiagram)
    }

    yamlFormat2(UmlSampleSolution)
  }

  override implicit def mapFormat[K: YamlFormat, V: YamlFormat] = new YamlFormat[Map[K, V]] {

    override def write(obj: Map[K, V]): YamlValue = ???

    /*      obj.toSeq.map {
          case (k, v) => YamlObject("key".toYaml -> k.toYaml, "value".toYaml -> v.toYaml)
        } */

    override def read(yaml: YamlValue): Map[K, V] = ???

    /*  yaml match {
      case YamlObject => ???
      case other      => deserializationError("")
    } */

  }


  val umlExerciseYamlFormat: YamlFormat[UmlExercise] = {
    implicit val svyf: YamlFormat[SemanticVersion] = semanticVersionYamlFormat

    implicit val esyf: YamlFormat[ExerciseState] = exerciseStateYamlFormat

    implicit val ussyf: YamlFormat[UmlSampleSolution] = umlSampleSolutionYamlFormat


    yamlFormat11(UmlExercise)
  }

}
