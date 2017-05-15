package model.matching;

import java.util.List;
import java.util.stream.Collectors;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;

public class MatchingResult<T> extends EvaluationResult {

  protected String matchName;

  protected List<Match<T>> matches;
  protected List<T> wrong;
  protected List<T> missing;

  public MatchingResult(String theMatchName, List<Match<T>> theMatches, List<T> theWrong, List<T> theMissing) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, analyze(theMatches, theWrong, theMissing));
    matchName = theMatchName;
    matches = theMatches;
    wrong = theWrong;
    missing = theMissing;
  }

  protected static <T> Success analyze(List<Match<T>> matches, List<T> wrong, List<T> missing) {
    boolean allMatched = wrong.isEmpty() && missing.isEmpty();
    boolean matchesOk = matches.parallelStream().allMatch(Match::isSuccessful);

    if(allMatched && matchesOk)
      return Success.COMPLETE;
    else if(allMatched || matchesOk)
      return Success.PARTIALLY;
    else
      return Success.NONE;
  }

  public List<Match<T>> getMatches() {
    return matches;
  }

  public String getMatchName() {
    return matchName;
  }

  public List<T> getMissing() {
    return missing;
  }

  public List<T> getWrong() {
    return wrong;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Korrekt: " + matches.stream().map(Match<T>::toString).collect(Collectors.toList()) + "\n");
    builder.append("Fehlend: " + missing.stream().map(T::toString).collect(Collectors.toList()) + "\n");
    builder.append("Falsch: " + wrong.stream().map(T::toString).collect(Collectors.toList()) + "\n");
    return builder.toString();
  }
}
