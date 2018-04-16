package model.uml

import model.uml.UmlConsts._
import model.uml.UmlEnums.{UmlAssociationType, UmlClassType, UmlMultiplicity}
import play.api.libs.functional.syntax._
import play.api.libs.json._

//noinspection ConvertibleToMethodValue
object UmlClassDiagramJsonFormat {

  implicit val enumWrites: Writes[Enum[_]] = (e: Enum[_]) => JsString(e.name)

  def enumReads[T <: Enum[T]](mkEnum: String => Option[T]): Reads[T] = {
    case JsString(s) => mkEnum(s) match {
      case Some(enumVal) => JsSuccess(enumVal)
      case None          => JsError("Not a valid enum value: " + s)
    }
    case v           => JsError("Can't convert to enum: " + v)
  }

  private implicit val umlClassTypeReads: Reads[UmlClassType] = enumReads(str => {
    if (str startsWith "uml.") {
      str match {
        case "uml.Abstract"  => Some(UmlClassType.ABSTRACT)
        case "uml.Class"     => Some(UmlClassType.CLASS)
        case "uml.Interface" => Some(UmlClassType.INTERFACE)
        case _               => None
      }
    } else UmlClassType.byString(str)
  })

  private implicit val umlMultiplicityReads: Reads[UmlMultiplicity] = enumReads(UmlMultiplicity.byString)

  private implicit val umlAssociationTypeReads: Reads[UmlAssociationType] = enumReads(UmlAssociationType.byString)


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


  private implicit val umlClassMemberReads: Reads[UmlClassMember] = (
    (__ \ "name").read[String] and
      (__ \ "type").read[String]
    ) (UmlClassMember.apply(_, _))

  private implicit val umlClassMemberWrites: Writes[UmlClassMember] = (
    (__ \ "name").write[String] and
      (__ \ "type").write[String]
    ) (unlift(UmlClassMember.unapply))


  private implicit val umlClassReads: Reads[UmlClass] = (
    (__ \ classTypeName).readWithDefault[UmlClassType](UmlClassType.CLASS) and
      (__ \ nameName).read[String] and
      (__ \ attributesName).readWithDefault[Seq[UmlClassMember]](Seq.empty) and
      (__ \ methodsName).readWithDefault[Seq[UmlClassMember]](Seq.empty) and
      (__ \ positionName).readNullable[Position]
    ) (UmlClass.apply(_, _, _, _, _))

  private implicit val umlClassWrites: Writes[UmlClass] = (
    (__ \ classTypeName).write[UmlClassType] and
      (__ \ nameName).write[String] and
      (__ \ attributesName).write[Seq[UmlClassMember]] and
      (__ \ methodsName).write[Seq[UmlClassMember]] and
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
