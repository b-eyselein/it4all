package model.tools.collectionTools.uml

import model.core.matching.MatchType
import model.tools.collectionTools.ToolTSInterfaceTypes
import model.tools.collectionTools.uml.UmlToolMain._
import model.tools.collectionTools.uml.matcher._
import nl.codestar.scalatsi.TypescriptType.TypescriptNamedType
import nl.codestar.scalatsi.{TSIType, TSType}

object UmlTSTypes extends ToolTSInterfaceTypes {

  private implicit val matchTypeTsType: TSType[MatchType]     = matchTypeTS
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

  private val umlClassMatchAnalysisResultTSI: TSIType[UmlClassMatchAnalysisResult] = {

    implicit val art: TSType[AttributeComparison] = matchingResultTSI("Attribute", {
      implicit val uat: TSIType[UmlAttribute] = umlAttributeTSI

      TSType.fromCaseClass[UmlAttributeMatch]
    })

    implicit val mrt: TSType[MethodComparison] = matchingResultTSI("Method", {
      implicit val umt: TSIType[UmlMethod] = umlMethodTSI

      TSType.fromCaseClass[UmlMethodMatch]
    })

    implicit val uctt: TSType[UmlClassType] = umlClassTypeTSType

    TSType.fromCaseClass
  }

  private val umlCompleteResultTSI: TSIType[UmlCompleteResult] = {
    implicit val crt: TSIType[ClassComparison] = matchingResultTSI(
      "Class", {
        implicit val uct: TSIType[UmlClass]                      = umlClassTSI
        implicit val ucart: TSIType[UmlClassMatchAnalysisResult] = umlClassMatchAnalysisResultTSI

        TSType.fromCaseClass[UmlClassMatch]
      }
    )

    implicit val art: TSIType[AssociationComparison] = matchingResultTSI(
      "Association", {
        implicit val uatt: TSType[UmlAssociationType] = umlAssociationTypeTSI
        implicit val uat: TSIType[UmlAssociation]     = umlAssociationTSI

        TSType.fromCaseClass[UmlAssociationMatch]
      }
    )

    implicit val irt: TSIType[ImplementationComparison] = matchingResultTSI("Implementation", {
      implicit val uit: TSIType[UmlImplementation] = umlImplementationTSI

      TSType.fromCaseClass[UmlImplementationMatch]
    })

    TSType.fromCaseClass
  }

  val exported: Seq[TypescriptNamedType] = Seq(
    umlClassDiagramTSI.get,
//    umlCompleteResultTSI.get
  )

}
