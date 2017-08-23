package model.querycorrectors;

import model.CorrectionException;
import model.matching.ScalaMatch;

import model.matching.MatchType;
import play.Logger;
import play.twirl.api.Html;

case class ColumnMatch(userArg: Option[ScalaColumnWrapper], sampleArg: Option[ScalaColumnWrapper]) extends ScalaMatch[ScalaColumnWrapper](userArg, sampleArg) {

  val hasAlias: Boolean

  val restMatched: Boolean

  val colNamesMatched = matchType == MatchType.SUCCESSFUL_MATCH || matchType == MatchType.UNSUCCESSFUL_MATCH

  //  public Html describe {
  //    // FIXME: todo...
  //    return views.html.resultTemplates.columnResult.render(this);
  //  }

  def getFirstColName = userArg.get.getColName;

  def getFirstRest = userArg.get.getRest;

  def getSecondColName = sampleArg.get.getColName;

  def getSecondRest = sampleArg.get.getRest;

  override def analyze(userArg: ScalaColumnWrapper, sampleArg: ScalaColumnWrapper) {
    //    try {
    //      return theArg1.match(theArg2);
    //    } catch (CorrectionException e) {
    //      Logger.error("There has been an error:", e);
    //      return MatchType.UNSUCCESSFUL_MATCH;
    //    }
  }

}
