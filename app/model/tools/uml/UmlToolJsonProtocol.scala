package model.tools.uml

import model.KeyValueObject
import model.tools.ToolJsonProtocol
import play.api.libs.json._

import scala.annotation.unused

object UmlToolJsonProtocol extends ToolJsonProtocol[UmlClassDiagram, UmlExerciseContent] {

  // class diagram format

  val umlAttributeFormat: OFormat[UmlAttribute] = Json.format
  val umlMethodFormat: OFormat[UmlMethod]       = Json.format

  private val umlClassFormat: Format[UmlClass] = {
    @unused implicit val uaf: Format[UmlAttribute] = umlAttributeFormat
    @unused implicit val umf: Format[UmlMethod]    = umlMethodFormat

    Json.format
  }

  private val umlImplementationFormat: Format[UmlImplementation] = Json.format

  private val umlAssociationFormat: Format[UmlAssociation] = Json.format

  private val umlClassDiagramFormat: Format[UmlClassDiagram] = {
    @unused implicit val ucf: Format[UmlClass]          = umlClassFormat
    @unused implicit val uif: Format[UmlImplementation] = umlImplementationFormat
    @unused implicit val uaf: Format[UmlAssociation]    = umlAssociationFormat

    Json.format[UmlClassDiagram]
  }

  // uml tool formats

  override val solutionInputFormat: Format[UmlClassDiagram] = umlClassDiagramFormat

  override val exerciseContentFormat: OFormat[UmlExerciseContent] = {
    @unused implicit val mf: Format[Map[String, String]] = KeyValueObject.mapFormat
    @unused implicit val ssf: Format[UmlClassDiagram]    = umlClassDiagramFormat

    Json.format
  }

}
