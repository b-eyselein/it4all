package model.tools.collectionTools

import model.core.matching._
import model.core.result.SuccessType
import nl.codestar.scalatsi.TypescriptType._
import nl.codestar.scalatsi.{DefaultTSTypes, TSIType, TSType}
import play.api.libs.json.JsValue


trait ToolTSInterfaceTypes extends DefaultTSTypes {

  import nl.codestar.scalatsi.dsl._

  def enumTsType[E <: enumeratum.EnumEntry, P <: enumeratum.Enum[E]](companion: P): TSType[E] =
    TSType.alias(companion.getClass.getSimpleName.replace("$", ""), TSUnion(companion.values.map(_.entryName)))

  protected val jsValueTsType: TSType[JsValue] = TSType.sameAs[JsValue, Any](anyTSType)

  protected val exerciseFileTSI: TSIType[ExerciseFile] = TSType.fromCaseClass[ExerciseFile] + ("active?" -> TSBoolean.get)

  protected val successTypeTS: TSType[SuccessType] = enumTsType(SuccessType)

  protected val matchTypeTS: TSType[MatchType] = enumTsType(MatchType)


  protected val stringMapTsType: TSType[Map[String, String]] = TSType.alias(
    "KeyValueObjectMap",
    TSArray(
      TSType.interface[KeyValueObject]("key" -> TSString, "value" -> TSString).get
    )
  )

  protected val baseMatchTSI: TSIType[Match[_, _]] = {
    val analysisResultTsInterface = TSType.interface("IAnalysisResult",
      "matchType" -> matchTypeTS.get
    )

    TSType.interface("IMatch",
      "userArg" -> TSUnion(Seq(TSAny, TSUndefined)),
      "sampleArg" -> TSUnion(Seq(TSAny, TSUndefined)),
      "analysisResult" -> TSUnion(Seq(analysisResultTsInterface.get, TSUndefined))
    )
  }

  protected val baseMatchingResultTSI: TSIType[MatchingResult[Object, GenericAnalysisResult, Match[Object, GenericAnalysisResult]]] = {

    TSType.interface(
      s"IMatchingResult",
      "matchName" -> TSString,
      "matchSingularName" -> TSString,
      "allMatches" -> TSArray(baseMatchTSI.get),
      "points" -> TSNumber,
      "maxPoints" -> TSNumber
    )
  }

  protected def matchingResultTSI[T, AR <: AnalysisResult, X <: Match[T, AR]](matchName: String, matchTSI: TSType[X]): TSIType[MatchingResult[T, AR, X]] = {
    // FIXME: implement!

    TSType.interface(
      s"I${matchName}MatchingResult",
      "matchName" -> TSString,
      "matchSingularName" -> TSString,
      "allMatches" -> TSArray(matchTSI.get),
      "points" -> TSNumber,
      "maxPoints" -> TSNumber
    )
  }

  protected def sampleSolutionTSI[SolType](solTypeTSI: TSType[SolType])(implicit x: Manifest[SampleSolution[SolType]]): TSIType[SampleSolution[SolType]] = {
    //    implicit val eft: TSIType[ExerciseFile] = exerciseFileTSI
    //    implicit val stt: TSType[SolType]       = solTypeTSI

    TSType.interface[SampleSolution[SolType]](
      "id" -> TSNumber,
      "sample" -> TSObject // solTypeTSI.get
    )
    //    TSType.fromCaseClass[SampleSolution[SolType]]
  }
}

