package model.dataExchange.json

import com.eclipsesource.schema.SchemaProp
import model.core.CoreConsts.successName
import model.core.matching._
import play.api.libs.json.{JsString, JsValue, Json}

final case class SchemaPropAnalysisResult(matchType: MatchType, schemaComparison: JsonSchemaComparisonResult) extends AnalysisResult {

  override def toJson: JsValue = Json.obj(
    successName -> matchType.entryName,
    "schemaComparison" -> schemaComparison.toJson
  )

}

final case class SchemaPropMatch(userArg: Option[SchemaProp], sampleArg: Option[SchemaProp]) extends Match {

  override type T = SchemaProp

  override type AR = SchemaPropAnalysisResult

  override protected def analyze(arg1: SchemaProp, arg2: SchemaProp): SchemaPropAnalysisResult = {
    val schemaComparison = JsonSchemaComparator.compare(arg1.schemaType, arg2.schemaType)

    // TODO: matchType from schemaComparison!
    val matchType = MatchType.PARTIAL_MATCH

    println(schemaComparison)
    SchemaPropAnalysisResult(matchType, schemaComparison)
  }

  override protected def descArgForJson(arg: SchemaProp): JsValue = {
    // ???
    // TODO: implement!
    JsString(arg.toString)
  }

  def subComparison: Option[JsonSchemaComparisonResult] = analysisResult.map(_.schemaComparison)

}

object SchemaPropMatcher extends Matcher[SchemaPropMatch] {

  override type T = SchemaProp

  override protected val matchName        : String = "Properties"
  override protected val matchSingularName: String = "der Property"

  override protected def canMatch(t1: SchemaProp, t2: SchemaProp): Boolean = t1.name == t2.name

  override protected def matchInstantiation(ua: Option[SchemaProp], sa: Option[SchemaProp]): SchemaPropMatch =
    SchemaPropMatch(ua, sa)

}
