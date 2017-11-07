package model.uml

import model.uml.UmlEnums.{Multiplicity, UmlAssociationType, UmlClassType}

case class UmlClass(classType: UmlClassType, name: String, attributes: List[String], methods: List[String])

case class UmlImplementation(subClass: String, superClass: String)

case class UmlAssociation(assocType: UmlAssociationType, ends: (UmlAssociationEnd, UmlAssociationEnd))

case class UmlAssociationEnd(endName: String, multiplicity: Multiplicity)
