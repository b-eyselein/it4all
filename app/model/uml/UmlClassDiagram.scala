package model.uml

import model.uml.UmlEnums._


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
