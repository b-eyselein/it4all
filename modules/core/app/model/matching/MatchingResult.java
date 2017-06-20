package model.matching;

import java.util.List;

import model.exercise.Success;
import model.result.EvaluationResult;
import play.Logger;

public class MatchingResult<T, M extends Match<T>> extends EvaluationResult {

  protected String matchName;

  protected List<M> matches;
  protected List<M> wrong;
  protected List<M> missing;

  public MatchingResult(String theMatchName, List<M> allMatches) {
    super(analyze(allMatches));
    matchName = theMatchName;
    matches = allMatches;
  }

  protected static <T, M extends Match<T>> Success analyze(List<M> matches) {
    boolean wrongMatches = matches.parallelStream().anyMatch(m -> m.matchType == MatchType.ONLY_USER);
    boolean missingMatches = matches.parallelStream().anyMatch(m -> m.matchType == MatchType.ONLY_SAMPLE);
    boolean oneMatchNotOk = matches.parallelStream().anyMatch(m -> m.matchType == MatchType.UNSUCCESSFUL_MATCH);

    if(wrongMatches || missingMatches)
      return Success.NONE;

    if(oneMatchNotOk)
      return Success.PARTIALLY;

    return Success.COMPLETE;
  }

  public String getGlyphicon() {
    Logger.debug(matchName + " (" + success + ") :: " + matches.size());
    switch(success) {
    case COMPLETE:
      return "glyphicon glyphicon-ok";
    case PARTIALLY:
      return "glyphicon glyphicon-question-sign";
    case NONE:
    case FAILURE:
    default:
      return "glyphicon glyphicon-remove";
    }
  }

  public List<M> getMatches() {
    return matches;
  }

  public String getMatchName() {
    return matchName;
  }

}
