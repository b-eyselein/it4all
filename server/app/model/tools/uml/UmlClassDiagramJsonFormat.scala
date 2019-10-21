package model.tools.uml

import play.api.libs.json._

object UmlClassDiagramJsonFormat {

  private val umlImplementationFormat: Format[UmlImplementation] = Json.format[UmlImplementation]

  private val umlAssociationFormat: Format[UmlAssociation] = Json.format[UmlAssociation]

  private val positionFormat: Format[MyPosition] = Json.format[MyPosition]


  private val umlAttributeFormat: Format[UmlAttribute] = Json.format[UmlAttribute]

  private val umlMethodFormat: Format[UmlMethod] = Json.format[UmlMethod]

  private val umlClassFormat: Format[UmlClass] = {
    implicit val uaf: Format[UmlAttribute] = umlAttributeFormat

    implicit val umf: Format[UmlMethod] = umlMethodFormat

    implicit val mpf: Format[MyPosition] = positionFormat

    Json.format[UmlClass]
  }


  val umlClassDiagramJsonFormat: Format[UmlClassDiagram] = {
    implicit val ucf: Format[UmlClass] = umlClassFormat

    implicit val uif: Format[UmlImplementation] = umlImplementationFormat

    implicit val uaf: Format[UmlAssociation] = umlAssociationFormat

    Json.format[UmlClassDiagram]
  }

}
