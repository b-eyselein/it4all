package model.tools.uml

import model.tools.uml.UmlConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json._

//noinspection ConvertibleToMethodValue
object UmlClassDiagramJsonFormat {

  private val umlClassTypeReads: Reads[UmlClassType] = {
    case JsString(str) => str match {
      case "uml.Abstract"                  => JsSuccess(UmlClassType.ABSTRACT)
      case "uml.Interface"                 => JsSuccess(UmlClassType.INTERFACE)
      case "uml.Class" | "customUml.Class" => JsSuccess(UmlClassType.CLASS)
      case _                               => UmlClassType.withNameInsensitiveOption(str) match {
        case Some(ct) => JsSuccess(ct)
        case None     => JsError("No such value: >>" + str + "<<")
      }
    }
    case _             => JsError("Needs to be a string!")
  }

  private implicit val umlVisibilityReads: Reads[UmlVisibility] = {
    case JsString(vis) =>
      UmlVisibility.values.find(_.representant == vis) match {
        case Some(visibility) => JsSuccess(visibility)
        case None             => JsError("No such value: >>" + vis + "<<")
      }
    case _             => JsError("Must be a string!")
  }

  private implicit val umlVisibilityWrites: Writes[UmlVisibility] = umlVis => JsString(umlVis.representant)

  private implicit val umlImplementationFormat: Format[UmlImplementation] = Json.format[UmlImplementation]

  private implicit val umlAssociationFormat: Format[UmlAssociation] = Json.format[UmlAssociation]

  private implicit val positionFormat: Format[MyPosition] = Json.format[MyPosition]

  private implicit val umlAttributeReads: Reads[UmlAttribute] = (
    (__ \ "visibility").read[UmlVisibility](umlVisibilityReads) and
      (__ \ "name").read[String] and
      (__ \ "type").read[String] and
      (__ \ "isStatic").readWithDefault[Boolean](false) and
      (__ \ "isDerived").readWithDefault[Boolean](false) and
      (__ \ "isAbstract").readWithDefault[Boolean](false)
    ) (UmlAttribute.apply(_, _, _, _, _, _))

  private implicit val umlAttributeWrites: Writes[UmlAttribute] = (
    (__ \ "visibility").write[UmlVisibility](umlVisibilityWrites) and
      (__ \ "name").write[String] and
      (__ \ "type").write[String] and
      (__ \ "isStatic").write[Boolean] and
      (__ \ "isDerived").write[Boolean] and
      (__ \ "isAbstract").write[Boolean]
    ) (unlift(UmlAttribute.unapply))

  private implicit val umlMethodReads: Reads[UmlMethod] = (
    (__ \ "visibility").read[UmlVisibility](umlVisibilityReads) and
      (__ \ "name").read[String] and
      (__ \ "type").read[String] and
      (__ \ "parameters").read[String] and
      (__ \ "isStatic").readWithDefault[Boolean](false) and
      (__ \ "isAbstract").readWithDefault[Boolean](false)
    ) (UmlMethod.apply(_, _, _, _, _, _))

  private implicit val umlMethodWrites: Writes[UmlMethod] = (
    (__ \ "visibility").write[UmlVisibility](umlVisibilityWrites) and
      (__ \ "name").write[String] and
      (__ \ "type").write[String] and
      (__ \ "parameters").write[String] and
      (__ \ "isStatic").write[Boolean] and
      (__ \ "isAbstract").write[Boolean]
    ) (unlift(UmlMethod.unapply))


  private implicit val umlClassReads: Reads[UmlClass] = (
    (__ \ classTypeName).readWithDefault[UmlClassType](UmlClassType.CLASS)(umlClassTypeReads) and
      (__ \ nameName).read[String] and
      (__ \ attributesName).readWithDefault[Seq[UmlAttribute]](Seq[UmlAttribute]()) and
      (__ \ methodsName).readWithDefault[Seq[UmlMethod]](Seq[UmlMethod]()) and
      (__ \ positionName).readNullable[MyPosition]
    ) (UmlClass.apply(_, _, _, _, _))

  private implicit val umlClassWrites: Writes[UmlClass] = (
    (__ \ classTypeName).write[UmlClassType] and
      (__ \ nameName).write[String] and
      (__ \ attributesName).write[Seq[UmlAttribute]] and
      (__ \ methodsName).write[Seq[UmlMethod]] and
      (__ \ positionName).writeNullable[MyPosition]
    ) (unlift(UmlClass.unapply))

  val umlSolutionJsonFormat: Format[UmlClassDiagram] = Json.format[UmlClassDiagram]

}
