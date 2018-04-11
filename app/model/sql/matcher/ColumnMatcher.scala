package model.sql.matcher

import model.Enums.MatchType
import model.Enums.MatchType._
import model.core.matching.{Match, Matcher, MatchingResult}
import model.sql.ColumnWrapper
import play.api.libs.json.{JsString, JsValue}

import scala.language.postfixOps


case class ColumnMatch(userArg: Option[ColumnWrapper], sampleArg: Option[ColumnWrapper]) extends Match[ColumnWrapper] {

  val hasAlias: Boolean = (userArg exists (_.hasAlias)) || (sampleArg exists (_.hasAlias))

  val restMatched: Boolean = false

  val colNamesMatched: Boolean = matchType == SUCCESSFUL_MATCH || matchType == UNSUCCESSFUL_MATCH

  val firstColName: String = userArg map (_.getColName) getOrElse ""

  val firstRest: String = userArg map (_.getRest) getOrElse ""

  val secondColName: String = sampleArg map (_.getColName) getOrElse ""

  val secondRest: String = sampleArg map (_.getRest) getOrElse ""

  override def analyze(userArg: ColumnWrapper, sampleArg: ColumnWrapper): MatchType = userArg doMatch sampleArg

  override protected def descArgForJson(arg: ColumnWrapper): JsValue = JsString(arg.toString)

}


object ColumnMatcher extends Matcher[ColumnWrapper, ColumnMatch, ColumnMatchingResult] {

  override protected def canMatch: (ColumnWrapper, ColumnWrapper) => Boolean = _ canMatch _


  override protected def matchInstantiation: (Option[ColumnWrapper], Option[ColumnWrapper]) => ColumnMatch = ColumnMatch


  override protected def resultInstantiation: Seq[ColumnMatch] => ColumnMatchingResult = ColumnMatchingResult

}

case class ColumnMatchingResult(allMatches: Seq[ColumnMatch]) extends MatchingResult[ColumnWrapper, ColumnMatch] {

  override val matchName: String = "Spalten"

}
