package model.tools.collectionTools.uml

import model.MyYamlProtocol
import net.jcazevedo.moultingyaml.{YamlFormat, DefaultYamlProtocol}

import scala.language.{implicitConversions, postfixOps}

object UmlExYamlProtocol extends MyYamlProtocol {

  import DefaultYamlProtocol._

  private val umlClassYamlFormat: YamlFormat[UmlClass] = {
    implicit val uctyf: YamlFormat[UmlClassType]  = new EnumYamlFormat(UmlClassType)
    implicit val uvyf : YamlFormat[UmlVisibility] = new EnumYamlFormat(UmlVisibility)
    implicit val uayf : YamlFormat[UmlAttribute]  = yamlFormat6(UmlAttribute)
    implicit val umyf : YamlFormat[UmlMethod]     = yamlFormat6(UmlMethod)

    yamlFormat4(UmlClass)
  }

  private val umlAssociationYamlFormat: YamlFormat[UmlAssociation] = {
    implicit val uatyf: YamlFormat[UmlAssociationType] = new EnumYamlFormat(UmlAssociationType)
    implicit val umyf : YamlFormat[UmlMultiplicity]    = new EnumYamlFormat[UmlMultiplicity](UmlMultiplicity)

    yamlFormat6(UmlAssociation)
  }

  private val umlSampleSolutionYamlFormat = {
    implicit val ucdyf: YamlFormat[UmlClassDiagram] = {
      implicit val ucyf: YamlFormat[UmlClass]          = umlClassYamlFormat
      implicit val uayf: YamlFormat[UmlAssociation]    = umlAssociationYamlFormat
      implicit val uiyf: YamlFormat[UmlImplementation] = yamlFormat2(UmlImplementation)

      yamlFormat3(UmlClassDiagram)
    }

    yamlFormat2(UmlSampleSolution)
  }


  val umlExerciseYamlFormat: YamlFormat[UmlExerciseContent] = {
    implicit val ussyf: YamlFormat[UmlSampleSolution] = umlSampleSolutionYamlFormat

    implicit def mapFormat[K: YamlFormat, V: YamlFormat]: YamlFormat[Map[K, V]] = myMapFormat

    yamlFormat3(UmlExerciseContent)
  }

}
