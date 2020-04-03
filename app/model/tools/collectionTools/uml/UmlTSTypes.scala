package model.tools.collectionTools.uml

import model.tools.collectionTools.ToolTSInterfaceTypes
import nl.codestar.scalatsi.{TSIType, TSType}

object UmlTSTypes extends ToolTSInterfaceTypes {

  private implicit val umlVisibilityTS: TSType[UmlVisibility] = enumTsType(UmlVisibility)

  private val umlAttributeTSI: TSIType[UmlAttribute] = TSType.fromCaseClass
  private val umlMethodTSI: TSIType[UmlMethod]       = TSType.fromCaseClass

  private val umlClassTypeTSType: TSType[UmlClassType] = enumTsType(UmlClassType)

  private val umlClassTSI: TSIType[UmlClass] = {
    implicit val uctt: TSType[UmlClassType] = umlClassTypeTSType

    implicit val uat: TSIType[UmlAttribute] = umlAttributeTSI
    implicit val umt: TSIType[UmlMethod]    = umlMethodTSI

    TSType.fromCaseClass
  }

  private val umlAssociationTypeTSI: TSType[UmlAssociationType] = enumTsType(UmlAssociationType)

  private val umlMultiplicityTSI: TSType[UmlMultiplicity] = enumTsType(UmlMultiplicity)

  private val umlAssociationTSI: TSIType[UmlAssociation] = {
    implicit val uatt: TSType[UmlAssociationType] = umlAssociationTypeTSI
    implicit val umt: TSType[UmlMultiplicity]     = umlMultiplicityTSI

    TSType.fromCaseClass
  }

  private val umlImplementationTSI: TSIType[UmlImplementation] = TSType.fromCaseClass

  val umlClassDiagramTSI: TSIType[UmlClassDiagram] = {
    implicit val uct: TSIType[UmlClass] = umlClassTSI

    implicit val uat: TSIType[UmlAssociation] = umlAssociationTSI

    implicit val uit: TSIType[UmlImplementation] = umlImplementationTSI

    TSType.fromCaseClass
  }

}
