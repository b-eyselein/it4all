package model.uml

import enumeratum.{EnumEntry, PlayEnum}

import scala.collection.immutable.IndexedSeq


sealed abstract class UmlVisibility(val representant: String) extends EnumEntry

object UmlVisibility extends PlayEnum[UmlVisibility] {

  override val values: IndexedSeq[UmlVisibility] = findValues

  case object PUBLIC extends UmlVisibility("+")

  case object PACKAGE extends UmlVisibility("~")

  case object PROTECTED extends UmlVisibility("#")

  case object PRIVATE extends UmlVisibility("-")

}


sealed abstract class UmlClassType(val german: String) extends EnumEntry

object UmlClassType extends PlayEnum[UmlClassType] {

  override val values: IndexedSeq[UmlClassType] = findValues

  case object CLASS extends UmlClassType("Klasse")

  case object INTERFACE extends UmlClassType("Interface")

  case object ABSTRACT extends UmlClassType("Abstrakte Klasse")

}


sealed abstract class UmlMultiplicity(val representant: String) extends EnumEntry

object UmlMultiplicity extends PlayEnum[UmlMultiplicity] {

  override val values: IndexedSeq[UmlMultiplicity] = findValues

  case object SINGLE extends UmlMultiplicity("1")

  case object UNBOUND extends UmlMultiplicity("*")

}


sealed abstract class UmlAssociationType(val german: String) extends EnumEntry

object UmlAssociationType extends PlayEnum[UmlAssociationType] {

  override val values: IndexedSeq[UmlAssociationType] = findValues

  case object ASSOCIATION extends UmlAssociationType("Assoziation")

  case object AGGREGATION extends UmlAssociationType("Aggregation")

  case object COMPOSITION extends UmlAssociationType("Komposition")

}


final case class Position(xCoord: Int, yCoord: Int)


sealed trait UmlClassMember {

  val visibility: UmlVisibility
  val memberName: String
  val memberType: String

  val isStatic  : Boolean
  val isAbstract: Boolean

}

final case class UmlAttribute(visibility: UmlVisibility, memberName: String, memberType: String, isStatic: Boolean, isDerived: Boolean, isAbstract: Boolean) extends UmlClassMember

final case class UmlMethod(visibility: UmlVisibility, memberName: String, memberType: String, parameters: String, isStatic: Boolean, isAbstract: Boolean) extends UmlClassMember


final case class UmlClass(classType: UmlClassType, className: String, attributes: Seq[UmlAttribute], methods: Seq[UmlMethod], position: Option[Position]) {

  def allMembers: Seq[UmlClassMember] = attributes ++ methods

}


final case class UmlAssociation(assocType: UmlAssociationType, assocName: Option[String], firstEnd: String, firstMult: UmlMultiplicity, secondEnd: String, secondMult: UmlMultiplicity) {

  def displayMult(turn: Boolean): String =
    if (turn) secondMult.representant + ":" + firstMult.representant
    else firstMult.representant + ":" + secondMult.representant

}


final case class UmlImplementation(subClass: String, superClass: String)


final case class UmlClassDiagram(classes: Seq[UmlClass], associations: Seq[UmlAssociation], implementations: Seq[UmlImplementation])
