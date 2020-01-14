package model.tools.collectionTools.uml

import play.api.libs.json._

object UmlClassDiagramJsonFormat {

  val umlClassFormat: Format[UmlClass] = {
    implicit val uaf: Format[UmlAttribute] = Json.format
    implicit val umf: Format[UmlMethod]    = Json.format

    Json.format
  }

  val umlImplementationFormat: Format[UmlImplementation] = Json.format

  val umlAssocationFormat: Format[UmlAssociation] = Json.format

  val umlClassDiagramJsonFormat: Format[UmlClassDiagram] = {

    implicit val ucf: Format[UmlClass] = umlClassFormat

    implicit val uif: Format[UmlImplementation] = umlImplementationFormat

    implicit val uaf: Format[UmlAssociation] = umlAssocationFormat

    Json.format[UmlClassDiagram]
  }

}
