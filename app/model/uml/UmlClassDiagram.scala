package model.uml

import model.uml.UmlEnums._


trait UmlClassDiagClassMember {

  val name      : String
  val memberType: String

  def render: String = name + ": " + memberType

}

case class UmlClassDiagClassAttribute(name: String, memberType: String) extends UmlClassDiagClassMember

case class UmlClassDiagClassMethod(name: String, memberType: String) extends UmlClassDiagClassMember


case class UmlClassDiagClass(classType: UmlClassType, className: String, attributes: Seq[UmlClassDiagClassAttribute], methods: Seq[UmlClassDiagClassMethod]) {

  def allMembers: Seq[UmlClassDiagClassMember] = attributes ++ methods

}


case class UmlClassDiagAssociation(assocType: UmlAssociationType, assocName: Option[String], firstEnd: String, firstMult: UmlMultiplicity, secondEnd: String, secondMult: UmlMultiplicity) {

  def displayMult(turn: Boolean): String =
    if (turn) secondMult.representant + ":" + firstMult.representant
    else firstMult.representant + ":" + secondMult.representant

}


case class UmlClassDiagImplementation(subClass: String, superClass: String)


case class UmlClassDiagram(classes: Seq[UmlClassDiagClass], associations: Seq[UmlClassDiagAssociation], implementations: Seq[UmlClassDiagImplementation])
