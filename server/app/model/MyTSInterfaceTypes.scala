package model

import model.core.LongText
import model.tools.collectionTools.regex._
import model.tools.collectionTools.sql.{SqlExerciseContent, SqlExerciseTag, SqlExerciseType}
import model.tools.collectionTools.{Exercise, ExerciseCollection}
import nl.codestar.scalatsi.TypescriptType.{TSArray, TSUnion}
import nl.codestar.scalatsi.{DefaultTSTypes, TSIType, TSType}
import play.api.libs.json.JsValue

object MyTSInterfaceTypes extends DefaultTSTypes {

  // FIXME: do not delete this import: import nl.codestar.scalatsi.TypescriptType.TSInterface

  import nl.codestar.scalatsi.TypescriptType.TSInterface
  import nl.codestar.scalatsi.dsl._

  private def enumTsType[E <: enumeratum.EnumEntry, P <: enumeratum.Enum[E]](companion: P): TSType[E] =
    TSType.alias(companion.getClass.getSimpleName.replace("$", ""), TSUnion(companion.values.map(_.entryName)))

  private val semanticVersionTSType: TSIType[SemanticVersion] = TSType.fromCaseClass[SemanticVersion]

  private val jsValueTsType : TSType[JsValue]  = TSType.sameAs[JsValue, Any]
  private val longTextTsType: TSType[LongText] = TSType.sameAs[LongText, String]

  implicit val exerciseTSI: TSIType[Exercise] = {
    implicit val svt : TSIType[SemanticVersion] = semanticVersionTSType
    implicit val ltt : TSType[LongText]         = longTextTsType
    implicit val jvtt: TSType[JsValue]          = jsValueTsType

    TSType.fromCaseClass[Exercise]
  }

  implicit val exerciseCollectionTSI: TSIType[ExerciseCollection] =
    TSType.fromCaseClass[ExerciseCollection] + ("exercises" -> TSArray(exerciseTSI.get))

  private val stringSampleSolutionTSI: TSIType[StringSampleSolution] = TSType.fromCaseClass[StringSampleSolution]

  implicit val regexExerciseContentTSI: TSIType[RegexExerciseContent] = {
    implicit val ssst : TSIType[StringSampleSolution]    = stringSampleSolutionTSI
    implicit val rmtdt: TSIType[RegexMatchTestData]      = TSType.fromCaseClass[RegexMatchTestData]
    implicit val retdt: TSIType[RegexExtractionTestData] = TSType.fromCaseClass[RegexExtractionTestData]
    implicit val rctt : TSType[RegexCorrectionType]      = enumTsType(RegexCorrectionTypes)

    TSType.fromCaseClass[RegexExerciseContent]
  }

  implicit val sqlExerciseContentTSI: TSIType[SqlExerciseContent] = {
    implicit val seTypeT: TSType[SqlExerciseType]       = enumTsType(SqlExerciseType)
    implicit val seTagT : TSType[SqlExerciseTag]        = enumTsType(SqlExerciseTag)
    implicit val ssst   : TSIType[StringSampleSolution] = stringSampleSolutionTSI

    TSType.fromCaseClass[SqlExerciseContent]
  }

}
