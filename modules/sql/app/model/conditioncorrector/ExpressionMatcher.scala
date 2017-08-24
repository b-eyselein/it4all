package model.conditioncorrector

import model.StringConsts
import model.matching.ScalaMatcher
import net.sf.jsqlparser.expression.Expression
import model.matching.ScalaMatchingResult

class ExpressionMatcher(userTableAliases: Map[String, String], sampleTableAliases: Map[String, String])
    extends ScalaMatcher[Expression, ExpressionMatch](StringConsts.CONDITIONS_NAME, new ExpressionBiPredicate(userTableAliases, sampleTableAliases), new ExpressionMatch(_, _)) {

  def matchExpressions(userExps: ExtractedExpressions, sampleExps: ExtractedExpressions): ScalaMatchingResult[Expression, ExpressionMatch] = {
    val binMatch = doMatch(userExps.binaryExpressions, sampleExps.binaryExpressions)

    val singleMatch = doMatch(userExps.otherExpressions, sampleExps.otherExpressions)

    ScalaMatchingResult.merge(binMatch, singleMatch)
  }

}