package model.tools.uml

import model.tools.ToolWithPartsJsonProtocol
import play.api.libs.json._

object UmlToolJsonProtocol extends ToolWithPartsJsonProtocol[UmlClassDiagram, UmlExerciseContent, UmlExPart] {

  // class diagram format

  val umlAttributeFormat: OFormat[UmlAttribute] = Json.format
  val umlMethodFormat: OFormat[UmlMethod]       = Json.format

  private val umlClassFormat: Format[UmlClass] = {
    implicit val uaf: Format[UmlAttribute] = umlAttributeFormat
    implicit val umf: Format[UmlMethod]    = umlMethodFormat

    Json.format
  }

  private val umlImplementationFormat: Format[UmlImplementation] = Json.format

  private val umlAssociationFormat: Format[UmlAssociation] = Json.format

  private val umlClassDiagramFormat: Format[UmlClassDiagram] = {

    implicit val ucf: Format[UmlClass] = umlClassFormat

    implicit val uif: Format[UmlImplementation] = umlImplementationFormat

    implicit val uaf: Format[UmlAssociation] = umlAssociationFormat

    Json.format[UmlClassDiagram]
  }

  // uml tool formats

  override val partTypeFormat: Format[UmlExPart] = UmlExPart.jsonFormat

  override val solutionInputFormat: Format[UmlClassDiagram] = umlClassDiagramFormat

  override val exerciseContentFormat: OFormat[UmlExerciseContent] = {
    implicit val mf: Format[Map[String, String]] = keyValueObjectMapFormat
    implicit val ssf: Format[UmlClassDiagram]    = umlClassDiagramFormat

    Json.format
  }

}
