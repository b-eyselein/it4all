package model.tools.uml

import play.api.libs.json._

object UmlClassDiagramJsonFormat {

  private val umlClassFormat: Format[UmlClass] = {
    implicit val uaf: Format[UmlAttribute] = Json.format
    implicit val umf: Format[UmlMethod]    = Json.format

    Json.format
  }

  private val umlImplementationFormat: Format[UmlImplementation] = Json.format

  private val umlAssociationFormat: Format[UmlAssociation] = Json.format

  val umlClassDiagramJsonFormat: Format[UmlClassDiagram] = {

    implicit val ucf: Format[UmlClass] = umlClassFormat

    implicit val uif: Format[UmlImplementation] = umlImplementationFormat

    implicit val uaf: Format[UmlAssociation] = umlAssociationFormat

    Json.format[UmlClassDiagram]
  }

}
