package model.uml

import model.uml.UmlConsts._
import model.uml.UmlEnums.{UmlAssociationType, UmlClassType, UmlMultiplicity}
import play.api.libs.functional.syntax._
import play.api.libs.json._

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


  private implicit val umlImplementationReads: Reads[UmlClassDiagImplementation] = (
    (__ \ subclassName).read[String] and
      (__ \ superclassName).read[String]
    ) (UmlClassDiagImplementation.apply _)

  private implicit val umlImplementationWrites: Writes[UmlClassDiagImplementation] = (
    (__ \ subclassName).write[String] and
      (__ \ superclassName).write[String]
    ) (unlift(UmlClassDiagImplementation.unapply))


  private implicit val umlAssociationReads: Reads[UmlClassDiagAssociation] = (
    (__ \ associationTypeName).read[UmlAssociationType] and
      (__ \ associationNameName).readNullable[String] and
      (__ \ firstEndName).read[String] and
      (__ \ firstMultName).read[UmlMultiplicity] and
      (__ \ secondEndName).read[String] and
      (__ \ secondMultName).read[UmlMultiplicity]
    ) (UmlClassDiagAssociation.apply _)

  private implicit val umlAssociationWrites: Writes[UmlClassDiagAssociation] = (
    (__ \ associationTypeName).write[UmlAssociationType] and
      (__ \ associationNameName).writeNullable[String] and
      (__ \ firstEndName).write[String] and
      (__ \ firstMultName).write[UmlMultiplicity] and
      (__ \ secondEndName).write[String] and
      (__ \ secondMultName).write[UmlMultiplicity]
    ) (unlift(UmlClassDiagAssociation.unapply))


  private implicit val umlMethodReads: Reads[UmlClassDiagClassMethod] = (
    (__ \ nameName).read[String] and
      (__ \ typeName).read[String]
    ) (UmlClassDiagClassMethod.apply _)

  private implicit val umlMethodWrites: Writes[UmlClassDiagClassMethod] = (
    (__ \ nameName).write[String] and
      (__ \ typeName).write[String]
    ) (unlift(UmlClassDiagClassMethod.unapply))


  private implicit val umlAttributeReads: Reads[UmlClassDiagClassAttribute] = (
    (__ \ nameName).read[String] and
      (__ \ typeName).read[String]
    ) (UmlClassDiagClassAttribute.apply _)

  private implicit val umlAttributeWrites: Writes[UmlClassDiagClassAttribute] = (
    (__ \ nameName).write[String] and
      (__ \ typeName).write[String]
    ) (unlift(UmlClassDiagClassAttribute.unapply))


  private implicit val umlClassReads: Reads[UmlClassDiagClass] = (
    (__ \ classTypeName).readWithDefault[UmlClassType](UmlClassType.CLASS) and
      (__ \ nameName).read[String] and
      (__ \ attributesName).readWithDefault[Seq[UmlClassDiagClassAttribute]](Seq.empty) and
      (__ \ methodsName).readWithDefault[Seq[UmlClassDiagClassMethod]](Seq.empty)
    ) (UmlClassDiagClass.apply _)

  private implicit val umlClassWrites: Writes[UmlClassDiagClass] = (
    (__ \ classTypeName).write[UmlClassType] and
      (__ \ nameName).write[String] and
      (__ \ attributesName).write[Seq[UmlClassDiagClassAttribute]] and
      (__ \ methodsName).write[Seq[UmlClassDiagClassMethod]]
    ) (unlift(UmlClassDiagClass.unapply))


  private implicit val umlSolutionReads: Reads[UmlClassDiagram] = (
    (__ \ classesName).read[Seq[UmlClassDiagClass]] and
      (__ \ associationsName).read[Seq[UmlClassDiagAssociation]] and
      (__ \ implementationsName).read[Seq[UmlClassDiagImplementation]]
    ) (UmlClassDiagram.apply _)


  private implicit val umlSolutionWrites: Writes[UmlClassDiagram] = (
    (__ \ classesName).write[Seq[UmlClassDiagClass]] and
      (__ \ associationsName).write[Seq[UmlClassDiagAssociation]] and
      (__ \ implementationsName).write[Seq[UmlClassDiagImplementation]]
    ) (unlift(UmlClassDiagram.unapply))

  val umlSolutionJsonFormat = Format(umlSolutionReads, umlSolutionWrites)


}
