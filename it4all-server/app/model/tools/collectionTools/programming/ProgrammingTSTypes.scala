package model.tools.collectionTools.programming

import model.tools.collectionTools.uml.{UmlClassDiagram, UmlTSTypes}
import model.tools.collectionTools.{ExerciseFile, SampleSolution, ToolTSInterfaceTypes}
import nl.codestar.scalatsi.{TSIType, TSType}
import play.api.libs.json.JsValue

trait ProgrammingTSTypes extends ToolTSInterfaceTypes {
  // Needs UmlClassDiagramTypes...
  self: UmlTSTypes =>

  import nl.codestar.scalatsi.TypescriptType.TSInterface

  private val progTestDataTSI: TSIType[ProgTestData] = {
    implicit val jvt: TSType[JsValue] = jsValueTsType
    TSType.fromCaseClass
  }

  val progSolutionTSI: TSIType[ProgSolution] = {
    implicit val eft : TSIType[ExerciseFile] = exerciseFileTSI
    implicit val ptdt: TSIType[ProgTestData] = progTestDataTSI

    TSType.fromCaseClass
  }

  lazy val progExerciseContentTSI: TSIType[ProgExerciseContent] = {
    implicit val progDataTypeTS: TSType[ProgDataType]                  = TSType.sameAs[ProgDataType, Any](anyTSType)
    implicit val pit           : TSIType[ProgInput]                    = TSType.fromCaseClass
    implicit val jvt           : TSType[JsValue]                       = jsValueTsType
    implicit val eft           : TSIType[ExerciseFile]                 = exerciseFileTSI
    implicit val sst           : TSIType[SampleSolution[ProgSolution]] = sampleSolutionTSI(progSolutionTSI)
    implicit val ucdt          : TSIType[UmlClassDiagram]              = umlClassDiagramTSI
    implicit val ptdt          : TSIType[ProgTestData]                 = progTestDataTSI
    implicit val uttt          : TSType[UnitTestType]                  = enumTsType(UnitTestTypes)
    implicit val uttct         : TSIType[UnitTestTestConfig]           = TSType.fromCaseClass

    TSType.fromCaseClass[ProgExerciseContent]
  }


}
