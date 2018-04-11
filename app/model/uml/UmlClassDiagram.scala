package model.uml

import model.uml.UmlEnums._


case class Position(xCoord: Int, yCoord: Int)


case class UmlClassDiagClass(classType: UmlClassType, className: String, attributes: Seq[String], methods: Seq[String], position: Option[Position]) {

  def allMembers: Seq[String] = attributes ++ methods

}


case class UmlClassDiagAssociation(assocType: UmlAssociationType, assocName: Option[String], firstEnd: String, firstMult: UmlMultiplicity, secondEnd: String, secondMult: UmlMultiplicity) {

  def displayMult(turn: Boolean): String =
    if (turn) secondMult.representant + ":" + firstMult.representant
    else firstMult.representant + ":" + secondMult.representant

}


case class UmlClassDiagImplementation(subClass: String, superClass: String)


case class UmlClassDiagram(classes: Seq[UmlClassDiagClass], associations: Seq[UmlClassDiagAssociation], implementations: Seq[UmlClassDiagImplementation])
