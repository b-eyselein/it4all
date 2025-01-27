package model.tools.uml

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}

import scala.collection.immutable.IndexedSeq

sealed abstract class UmlVisibility(val representant: String) extends EnumEntry

object UmlVisibility extends Enum[UmlVisibility] with PlayJsonEnum[UmlVisibility] {

  case object PUBLIC    extends UmlVisibility("+")
  case object PACKAGE   extends UmlVisibility("~")
  case object PROTECTED extends UmlVisibility("#")
  case object PRIVATE   extends UmlVisibility("-")

  override val values: IndexedSeq[UmlVisibility] = findValues

}

sealed abstract class UmlClassType(val german: String) extends EnumEntry

object UmlClassType extends Enum[UmlClassType] with PlayJsonEnum[UmlClassType] {

  case object CLASS     extends UmlClassType("Klasse")
  case object INTERFACE extends UmlClassType("Interface")
  case object ABSTRACT  extends UmlClassType("Abstrakte Klasse")

  override val values: IndexedSeq[UmlClassType] = findValues

}

sealed abstract class UmlMultiplicity(val representant: String) extends EnumEntry

object UmlMultiplicity extends Enum[UmlMultiplicity] with PlayJsonEnum[UmlMultiplicity] {

  case object SINGLE  extends UmlMultiplicity("1")
  case object UNBOUND extends UmlMultiplicity("*")

  override val values: IndexedSeq[UmlMultiplicity] = findValues

}

sealed abstract class UmlAssociationType(val german: String) extends EnumEntry

object UmlAssociationType extends Enum[UmlAssociationType] with PlayJsonEnum[UmlAssociationType] {

  case object ASSOCIATION extends UmlAssociationType("Assoziation")
  case object AGGREGATION extends UmlAssociationType("Aggregation")
  case object COMPOSITION extends UmlAssociationType("Komposition")

  override val values: IndexedSeq[UmlAssociationType] = findValues

}

sealed trait UmlClassMember {

  val visibility: UmlVisibility
  val memberName: String
  val memberType: String

  val isStatic: Boolean
  val isAbstract: Boolean

}

final case class UmlAttribute(
  visibility: UmlVisibility = UmlVisibility.PUBLIC,
  memberName: String,
  memberType: String,
  isStatic: Boolean = false,
  isDerived: Boolean = false,
  isAbstract: Boolean = false
) extends UmlClassMember

final case class UmlMethod(
  visibility: UmlVisibility = UmlVisibility.PUBLIC,
  memberName: String,
  memberType: String,
  parameters: String,
  isStatic: Boolean = false,
  isAbstract: Boolean = false
) extends UmlClassMember

final case class UmlClass(
  classType: UmlClassType = UmlClassType.CLASS,
  name: String,
  attributes: Seq[UmlAttribute] = Seq.empty,
  methods: Seq[UmlMethod] = Seq.empty
) {

  def allMembers: Seq[UmlClassMember] = attributes ++ methods

}

final case class UmlAssociation(
  assocType: UmlAssociationType = UmlAssociationType.ASSOCIATION,
  assocName: Option[String] = None,
  firstEnd: String,
  firstMult: UmlMultiplicity,
  secondEnd: String,
  secondMult: UmlMultiplicity
) {

  def displayMult(turn: Boolean): String =
    if (turn) secondMult.representant + ":" + firstMult.representant
    else firstMult.representant + ":" + secondMult.representant

}

final case class UmlImplementation(subClass: String, superClass: String)

final case class UmlClassDiagram(
  classes: Seq[UmlClass],
  associations: Seq[UmlAssociation],
  implementations: Seq[UmlImplementation]
)
