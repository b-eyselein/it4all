package model.sql.matcher

import model.core.matching._
import model.sql.ColumnWrapper
import play.api.libs.json.{JsString, JsValue}

import scala.language.postfixOps


case class ColumnMatch(userArg: Option[ColumnWrapper], sampleArg: Option[ColumnWrapper]) extends Match[ColumnWrapper, GenericAnalysisResult] {

  //  override type MatchAnalysisResult = GenericAnalysisResult

  val hasAlias: Boolean = (userArg exists (_.hasAlias)) || (sampleArg exists (_.hasAlias))

  val restMatched: Boolean = false

  val colNamesMatched: Boolean = matchType == MatchType.SUCCESSFUL_MATCH || matchType == MatchType.UNSUCCESSFUL_MATCH

  val firstColName: String = userArg map (_.getColName) getOrElse ""

  val firstRest: String = userArg map (_.getRest) getOrElse ""

  val secondColName: String = sampleArg map (_.getColName) getOrElse ""

  val secondRest: String = sampleArg map (_.getRest) getOrElse ""

  override def analyze(userArg: ColumnWrapper, sampleArg: ColumnWrapper): GenericAnalysisResult = GenericAnalysisResult(userArg doMatch sampleArg)

  override protected def descArgForJson(arg: ColumnWrapper): JsValue = JsString(arg.toString)

}


object ColumnMatcher extends Matcher[ColumnWrapper, GenericAnalysisResult, ColumnMatch] {

  override protected def canMatch: (ColumnWrapper, ColumnWrapper) => Boolean = _ canMatch _


  override protected def matchInstantiation: (Option[ColumnWrapper], Option[ColumnWrapper]) => ColumnMatch = ColumnMatch

}
