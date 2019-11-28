package model

import de.uniwue.webtester.{HtmlTask, JsActionType, JsHtmlElementSpec, JsTask}
import model.core.LongText
import model.tools.collectionTools.programming.{ProgSolution, ProgTestData}
import model.tools.collectionTools.regex._
import model.tools.collectionTools.sql.{SqlExerciseContent, SqlExerciseTag, SqlExerciseType}
import model.tools.collectionTools.uml._
import model.tools.collectionTools.web.WebExerciseContent
import model.tools.collectionTools.xml.{XmlExerciseContent, XmlSolution}
import model.tools.collectionTools.{Exercise, ExerciseCollection, ExerciseFile, SampleSolution}
import nl.codestar.scalatsi.TypescriptType._
import nl.codestar.scalatsi.{DefaultTSTypes, TSIType, TSType}
import play.api.libs.json.JsValue

object MyTSInterfaceTypes extends DefaultTSTypes {

  // FIXME: do not delete this import: import nl.codestar.scalatsi.TypescriptType.TSInterface

  import nl.codestar.scalatsi.dsl._

  private def enumTsType[E <: enumeratum.EnumEntry, P <: enumeratum.Enum[E]](companion: P): TSType[E] =
    TSType.alias(companion.getClass.getSimpleName.replace("$", ""), TSUnion(companion.values.map(_.entryName)))

  private val semanticVersionTSType: TSIType[SemanticVersion] = TSType.fromCaseClass[SemanticVersion]

  private val jsValueTsType : TSType[JsValue]  = TSType.sameAs[JsValue, Any]
  private val longTextTsType: TSType[LongText] = TSType.sameAs[LongText, String]

  private val exerciseFileTSI: TSIType[ExerciseFile] = TSType.fromCaseClass[ExerciseFile] + ("active?" -> TSBoolean.get)

  private def sampleSolutionTSI[SolType](solTypeTSI: TSType[SolType])(implicit x: Manifest[SampleSolution[SolType]]): TSIType[SampleSolution[SolType]] = {
    //    implicit val eft: TSIType[ExerciseFile] = exerciseFileTSI
    //    implicit val stt: TSType[SolType]       = solTypeTSI

    TSType.interface[SampleSolution[SolType]](
      "id" -> TSNumber,
      "sample" -> TSObject // solTypeTSI.get
    )
    //    TSType.fromCaseClass[SampleSolution[SolType]]
  }

  // Collections, Exercises and ExerciseContents

  implicit val exerciseTSI: TSIType[Exercise] = {
    implicit val svt : TSIType[SemanticVersion] = semanticVersionTSType
    implicit val ltt : TSType[LongText]         = longTextTsType
    implicit val jvtt: TSType[JsValue]          = jsValueTsType

    TSType.fromCaseClass[Exercise]
  }

  implicit val exerciseCollectionTSI: TSIType[ExerciseCollection] =
    TSType.fromCaseClass[ExerciseCollection] + ("exercises" -> TSArray(exerciseTSI.get))

  //  implicit val progExerciseContentTSI: TSIType[ProgExerciseContent] = {
  //
  //   TSType.fromCaseClass[ProgExerciseContent]
  //}

  implicit val progSolutionTSI: TSIType[ProgSolution] = {
    implicit val eft : TSIType[ExerciseFile] = exerciseFileTSI
    implicit val ptdt: TSIType[ProgTestData] = {
      implicit val jvt: TSType[JsValue] = jsValueTsType
      TSType.fromCaseClass[ProgTestData]
    }

    TSType.fromCaseClass[ProgSolution]
  }

  implicit val regexExerciseContentTSI: TSIType[RegexExerciseContent] = {
    implicit val ssst : TSIType[SampleSolution[String]]  = sampleSolutionTSI[String](TSType(TSString))
    implicit val rmtdt: TSIType[RegexMatchTestData]      = TSType.fromCaseClass[RegexMatchTestData]
    implicit val retdt: TSIType[RegexExtractionTestData] = TSType.fromCaseClass[RegexExtractionTestData]
    implicit val rctt : TSType[RegexCorrectionType]      = enumTsType(RegexCorrectionTypes)

    TSType.fromCaseClass[RegexExerciseContent]
  }

  implicit val sqlExerciseContentTSI: TSIType[SqlExerciseContent] = {
    implicit val seTypeT: TSType[SqlExerciseType]         = enumTsType(SqlExerciseType)
    implicit val seTagT : TSType[SqlExerciseTag]          = enumTsType(SqlExerciseTag)
    implicit val ssst   : TSIType[SampleSolution[String]] = sampleSolutionTSI[String](TSType(TSString))

    TSType.fromCaseClass[SqlExerciseContent]
  }

  implicit val umlClassDiagramTSI: TSIType[UmlClassDiagram] = {
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

  implicit val umlExerciseContentTSI: TSIType[UmlExerciseContent] = {
    implicit val usst: TSIType[SampleSolution[UmlClassDiagram]] = sampleSolutionTSI[UmlClassDiagram](umlClassDiagramTSI)

    TSType.fromCaseClass[UmlExerciseContent]
  }

  implicit val webExerciseContentTSI: TSIType[WebExerciseContent] = {
    implicit val eft : TSIType[ExerciseFile]                      = exerciseFileTSI
    implicit val htt : TSIType[HtmlTask]                          = TSType.fromCaseClass[HtmlTask]
    implicit val jtt : TSIType[JsTask]                            = {
      implicit val jatt : TSType[JsActionType]       = enumTsType(JsActionType)
      implicit val jhest: TSIType[JsHtmlElementSpec] = TSType.fromCaseClass[JsHtmlElementSpec]
      TSType.fromCaseClass[JsTask]
    }
    implicit val fsst: TSIType[SampleSolution[Seq[ExerciseFile]]] = sampleSolutionTSI(TSType(TSArray(exerciseFileTSI.get)))


    TSType.fromCaseClass[WebExerciseContent]
  }

  implicit val xmlExerciseContentTSI: TSIType[XmlExerciseContent] = {
    implicit val xsst: TSIType[SampleSolution[XmlSolution]] = sampleSolutionTSI[XmlSolution](TSType.fromCaseClass[XmlSolution])

    TSType.fromCaseClass[XmlExerciseContent]
  }
}
