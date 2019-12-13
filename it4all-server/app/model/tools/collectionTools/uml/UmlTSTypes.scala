package model.tools.collectionTools.uml

import model.tools.collectionTools.{SampleSolution, ToolTSInterfaceTypes}
import nl.codestar.scalatsi.{TSIType, TSType}
import nl.codestar.scalatsi.TypescriptType.TSInterface

trait UmlTSTypes extends ToolTSInterfaceTypes {

  val umlClassDiagramTSI: TSIType[UmlClassDiagram] = {
    implicit val uct: TSIType[UmlClass] = {
      implicit val uctt: TSType[UmlClassType]  = enumTsType(UmlClassType)
      implicit val uvt : TSType[UmlVisibility] = enumTsType(UmlVisibility)

      implicit val uat: TSIType[UmlAttribute] = TSType.fromCaseClass[UmlAttribute]
      implicit val umt: TSIType[UmlMethod]    = TSType.fromCaseClass[UmlMethod]

      TSType.fromCaseClass[UmlClass]
    }

    implicit val uat: TSIType[UmlAssociation] = {
      implicit val uatt: TSType[UmlAssociationType] = enumTsType(UmlAssociationType)
      implicit val umt : TSType[UmlMultiplicity]    = enumTsType(UmlMultiplicity)

      TSType.fromCaseClass[UmlAssociation]
    }

    implicit val uit: TSIType[UmlImplementation] = TSType.fromCaseClass[UmlImplementation]

    TSType.fromCaseClass[UmlClassDiagram]
  }

  val umlExerciseContentTSI: TSIType[UmlExerciseContent] = {
    implicit val usst: TSIType[SampleSolution[UmlClassDiagram]] = sampleSolutionTSI[UmlClassDiagram](umlClassDiagramTSI)

    TSType.fromCaseClass[UmlExerciseContent]
  }

}
