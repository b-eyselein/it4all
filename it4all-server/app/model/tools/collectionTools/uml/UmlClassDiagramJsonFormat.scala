package model.tools.collectionTools.uml

import play.api.libs.json._

object UmlClassDiagramJsonFormat {

  val umlClassDiagramJsonFormat: Format[UmlClassDiagram] = {

    implicit val ucf: Format[UmlClass] = {
      implicit val uaf: Format[UmlAttribute] = Json.format[UmlAttribute]

      implicit val umf: Format[UmlMethod] = Json.format[UmlMethod]

      Json.format[UmlClass]
    }

    implicit val uif: Format[UmlImplementation] = Json.format[UmlImplementation]

    implicit val uaf: Format[UmlAssociation] = Json.format[UmlAssociation]

    Json.format[UmlClassDiagram]
  }

}
