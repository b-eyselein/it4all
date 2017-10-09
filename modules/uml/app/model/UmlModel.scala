package model

import scala.collection.JavaConverters.seqAsJavaListConverter

// Types of classes

sealed class UmlClassType(val name: String)

case object Class extends UmlClassType("Klasse")

case object Interface extends UmlClassType("Interface")

case object AbstractClass extends UmlClassType("Abstrakte Klasse")

object UmlClassType {
  def fromString(str: String): UmlClassType = str match {
    case "CLASS"     => Class
    case "INTERFACE" => Interface
    case "ABSTRACT"  => AbstractClass
    case _           => null
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
    case "1" | "SINGLE"  => SINGLE
    case "*" | "UNBOUND" => UNBOUND
    case _               => throw new IllegalArgumentException(s"Value $str is not allowed for a Multiplicity!")
  }

}

// Ends

case class UmlAssociationEnd(endName: String, multiplicity: Multiplicity)

// Types of associations

sealed class UmlAssociationType(val germanName: String, val name: String)

case object Association extends UmlAssociationType("Assoziation", "ASSOCIATION")

case object Aggregation extends UmlAssociationType("Aggregation", "AGGREGATION")

case object Composition extends UmlAssociationType("Komposition", "COMPOSITION")

object UmlAssociationType {

  def getByString(str: String): UmlAssociationType = str match {
    case "ASSOCIATION" => Association
    case "AGGREGATION" => Aggregation
    case "COMPOSITION" => Composition
  }

}

// Association

case class UmlAssociation(val assocType: UmlAssociationType, val ends: (UmlAssociationEnd, UmlAssociationEnd)) 
