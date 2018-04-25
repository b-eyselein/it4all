package model.uml

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}

import scala.collection.immutable.IndexedSeq

sealed abstract class UmlClassType(val german: String) extends EnumEntry

object UmlClassType extends Enum[UmlClassType] with PlayJsonEnum[UmlClassType] {

  val values: IndexedSeq[UmlClassType] = findValues

  case object CLASS extends UmlClassType("Klasse")

  case object INTERFACE extends UmlClassType("Interface")

  case object ABSTRACT extends UmlClassType("Abstrakte Klasse")

}

sealed abstract class UmlMultiplicity(val representant: String) extends EnumEntry

object UmlMultiplicity extends Enum[UmlMultiplicity] with PlayJsonEnum[UmlMultiplicity] {

  val values: IndexedSeq[UmlMultiplicity] = findValues

  case object SINGLE extends UmlMultiplicity("1")

  case object UNBOUND extends UmlMultiplicity("*")

}

sealed abstract class UmlAssociationType(val german: String) extends EnumEntry

object UmlAssociationType extends Enum[UmlAssociationType] with PlayJsonEnum[UmlAssociationType] {

  val values: IndexedSeq[UmlAssociationType] = findValues

  case object ASSOCIATION extends UmlAssociationType("Assoziation")

  case object AGGREGATION extends UmlAssociationType("Aggregation")

  case object COMPOSITION extends UmlAssociationType("Komposition")

}


case class Position(xCoord: Int, yCoord: Int)


case class UmlClassMember(memberName: String, memberType: String)


case class UmlClass(classType: UmlClassType, className: String, attributes: Seq[UmlClassMember], methods: Seq[UmlClassMember], position: Option[Position]) {

  def allMembers: Seq[UmlClassMember] = attributes ++ methods

}


case class UmlAssociation(assocType: UmlAssociationType, assocName: Option[String], firstEnd: String, firstMult: UmlMultiplicity, secondEnd: String, secondMult: UmlMultiplicity) {

  def displayMult(turn: Boolean): String =
    if (turn) secondMult.representant + ":" + firstMult.representant
    else firstMult.representant + ":" + secondMult.representant

}


case class UmlImplementation(subClass: String, superClass: String)


case class UmlClassDiagram(classes: Seq[UmlClass], associations: Seq[UmlAssociation], implementations: Seq[UmlImplementation])
