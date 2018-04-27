package model.uml

import model.uml.UmlConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json._

//noinspection ConvertibleToMethodValue
object UmlClassDiagramJsonFormat {

  private val umlClassTypeReads: Reads[UmlClassType] = {
    case JsString(str) => str match {
      case "uml.Abstract"  => JsSuccess(UmlClassType.ABSTRACT)
      case "uml.Interface" => JsSuccess(UmlClassType.INTERFACE)
      case "uml.Class"     => JsSuccess(UmlClassType.CLASS)
      case _               => UmlClassType.withNameInsensitiveOption(str) match {
        case Some(ct) => JsSuccess(ct)
        case None     => JsError("No such value: " + str)
      }
    }
    case _             => JsError("Needs to be a string!")
  }

  private val umlVisibilityReads: Reads[UmlVisibility] = {
    case JsString(str) => UmlVisibility.withNameInsensitiveOption(str) match {
      case Some(vis) => JsSuccess(vis)
      case None      => JsError("No such value " + str)
    }
    case _             => JsError("Needs to be a string!")
  }

  private implicit val umlImplementationReads: Reads[UmlImplementation] = (
    (__ \ subClassName).read[String] and
      (__ \ superClassName).read[String]
    ) (UmlImplementation.apply(_, _))

  private implicit val umlImplementationWrites: Writes[UmlImplementation] = (
    (__ \ subClassName).write[String] and
      (__ \ superClassName).write[String]
    ) (unlift(UmlImplementation.unapply))


  private implicit val umlAssociationReads: Reads[UmlAssociation] = (
    (__ \ associationTypeName).read[UmlAssociationType] and
      (__ \ associationNameName).readNullable[String] and
      (__ \ firstEndName).read[String] and
      (__ \ firstMultName).read[UmlMultiplicity] and
      (__ \ secondEndName).read[String] and
      (__ \ secondMultName).read[UmlMultiplicity]
    ) (UmlAssociation.apply(_, _, _, _, _, _))

  private implicit val umlAssociationWrites: Writes[UmlAssociation] = (
    (__ \ associationTypeName).write[UmlAssociationType] and
      (__ \ associationNameName).writeNullable[String] and
      (__ \ firstEndName).write[String] and
      (__ \ firstMultName).write[UmlMultiplicity] and
      (__ \ secondEndName).write[String] and
      (__ \ secondMultName).write[UmlMultiplicity]
    ) (unlift(UmlAssociation.unapply))


  private implicit val positionReads: Reads[Position] = (
    (__ \ "x").read[Int] and
      (__ \ "y").read[Int]
    ) (Position.apply(_, _))

  private implicit val positionWrites: Writes[Position] = (
    (__ \ "x").write[Int] and
      (__ \ "y").write[Int]
    ) (unlift(Position.unapply))


  private implicit val umlAttributeReads: Reads[UmlAttribute] = (
    (__ \ "visibility").read[UmlVisibility](umlVisibilityReads) and
      (__ \ "name").read[String] and
      (__ \ "type").read[String] and
      (__ \ "isStatic").readWithDefault[Boolean](false) and
      (__ \ "isDerived").readWithDefault[Boolean](false) and
      (__ \ "isAbstract").readWithDefault[Boolean](false)
    ) (UmlAttribute.apply(_, _, _, _, _, _))

  private implicit val umlAttributeWrites: Writes[UmlAttribute] = (
    (__ \ "visibility").write[UmlVisibility] and
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
    (__ \ "visibility").write[UmlVisibility] and
      (__ \ "name").write[String] and
      (__ \ "type").write[String] and
      (__ \ "parameters").write[String] and
      (__ \ "isStatic").write[Boolean] and
      (__ \ "isAbstract").write[Boolean]
    ) (unlift(UmlMethod.unapply))


  private implicit val umlClassReads: Reads[UmlClass] = (
    (__ \ classTypeName).readWithDefault[UmlClassType](UmlClassType.CLASS)(umlClassTypeReads) and
      (__ \ nameName).read[String] and
      (__ \ attributesName).readWithDefault[Seq[UmlAttribute]](Seq.empty) and
      (__ \ methodsName).readWithDefault[Seq[UmlMethod]](Seq.empty) and
      (__ \ positionName).readNullable[Position]
    ) (UmlClass.apply(_, _, _, _, _))

  private implicit val umlClassWrites: Writes[UmlClass] = (
    (__ \ classTypeName).write[UmlClassType] and
      (__ \ nameName).write[String] and
      (__ \ attributesName).write[Seq[UmlAttribute]] and
      (__ \ methodsName).write[Seq[UmlMethod]] and
      (__ \ positionName).writeNullable[Position]
    ) (unlift(UmlClass.unapply))


  private implicit val umlSolutionReads: Reads[UmlClassDiagram] = (
    (__ \ classesName).read[Seq[UmlClass]] and
      (__ \ associationsName).read[Seq[UmlAssociation]] and
      (__ \ implementationsName).read[Seq[UmlImplementation]]
    ) (UmlClassDiagram.apply(_, _, _))


  private implicit val umlSolutionWrites: Writes[UmlClassDiagram] = (
    (__ \ classesName).write[Seq[UmlClass]] and
      (__ \ associationsName).write[Seq[UmlAssociation]] and
      (__ \ implementationsName).write[Seq[UmlImplementation]]
    ) (unlift(UmlClassDiagram.unapply))

  val umlSolutionJsonFormat = Format(umlSolutionReads, umlSolutionWrites)


}
