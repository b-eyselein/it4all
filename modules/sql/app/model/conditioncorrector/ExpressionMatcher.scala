package model.conditioncorrector;

import java.util.Map

import model.StringConsts
import model.matching.MatchingResult
import model.matching.ScalaMatcher
import net.sf.jsqlparser.expression.Expression

class ExpressionMatcher(userTableAliases: Map[String, String], sampleTableAliases: Map[String, String])
    extends ScalaMatcher[Expression, ExpressionMatch](StringConsts.CONDITIONS_NAME, new ExpressionBiPredicate(userTableAliases, sampleTableAliases), new ExpressionMatch(_, _)) {

  def matchExpressions(userExps: ExtractedExpressions, sampleExps: ExtractedExpressions): MatchingResult[Expression, ExpressionMatch] = {
    //    MatchingResult[Expression, ExpressionMatch] binMatch = match(userExps.getBinaryExpressions(),
    //        sampleExps.getBinaryExpressions());
    //    
    //    MatchingResult[Expression, ExpressionMatch] singleMatch = match(userExps.getOtherExpressions(),
    //        sampleExps.getOtherExpressions());
    //    
    //    return MatchingResult.merge(binMatch, singleMatch);
    null
  }

}