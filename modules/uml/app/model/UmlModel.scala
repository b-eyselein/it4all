package model

import scala.collection.JavaConverters._

// Types of classes

sealed class UmlClassType(val name: String)

case object Class extends UmlClassType("Klasse")

case object Interface extends UmlClassType("Interface")

case object AbstractClass extends UmlClassType("Abstrakte Klasse")

object UmlClassType {
  def fromString(str: String): UmlClassType = str match {
    case "CLASS" => Class
    case "INTERFACE" => Interface
    case "ABSTRACT" => AbstractClass
    case _ => null
  }
}

// Class

case class UmlClass(classType: UmlClassType, name: String, attributes: List[String], methods: List[String]) {

  def attrsForJava: java.util.List[String] = attributes.asJava

  def methodsForJava: java.util.List[String] = methods.asJava

}

case class UmlImplementation(subClass: String, superClass: String)

// Multiplicity

sealed class Multiplicity(val representant: String)

case object SINGLE extends Multiplicity("1")

case object UNBOUND extends Multiplicity("*")

object Multiplicity {

  def getByString(str: String): Multiplicity = str match {
    case "1" | "SINGLE" => SINGLE
    case "*" | "UNBOUND" => UNBOUND
    case _ => throw new IllegalArgumentException(s"Value $str is not allowed for a Multiplicity!")
  }

}

// Ends

case class UmlAssociationEnd(endName: String, multiplicity: Multiplicity)

// Types of associations

sealed class UmlAssociationType(name: String)

case object Association extends UmlAssociationType("ASSOCIATION")

case object Aggregation extends UmlAssociationType("AGGREGATION")

case object Composition extends UmlAssociationType("COMPOSITION")

object UmlAssociationType {

  def getByString(str: String): UmlAssociationType = str match {
    case "ASSOCIATION" => Association
    case "AGGREGATION" => Aggregation
    case "COMPOSITION" => Composition
  }

}

// Association

case class UmlAssociation(val assocType: UmlAssociationType, val ends: (UmlAssociationEnd, UmlAssociationEnd)) {

  def multsAsString(end1: UmlAssociationEnd, end2: UmlAssociationEnd) =
    s"${ends._1.multiplicity.representant} : ${ends._2.multiplicity.representant}"

  def multsAsString(switchOrder: Boolean = false): String = if (switchOrder)
    multsAsString(ends._2, ends._1)
  else
    multsAsString(ends._1, ends._2)

}
