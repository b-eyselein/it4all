package model.matching;

import java.util.List;
import java.util.stream.Collectors;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;

public class MatchingResult<T> extends EvaluationResult {

  protected List<Match<T>> matches;
  protected List<T> wrong;
  protected List<T> missing;

  public MatchingResult(List<Match<T>> theMatches, List<T> theWrong, List<T> theMissing) {
    super(FeedbackLevel.MINIMAL_FEEDBACK, Success.NONE);
    matches = theMatches;
    wrong = theWrong;
    missing = theMissing;

    analyze();
  }

  public List<Match<T>> getCorrect() {
    return matches;
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

    builder.append("Korrekt: " + matches.stream().map(Match<T>::toString).collect(Collectors.toList()));
    builder.append("\n");
    builder.append("Fehlend: " + missing.stream().map(T::toString).collect(Collectors.toList()));
    builder.append("\n");
    builder.append("Falsch: " + wrong.stream().map(T::toString).collect(Collectors.toList()));

    return builder.toString();
  }

  protected void analyze() {
    boolean allMatched = wrong.isEmpty() && missing.isEmpty();
    boolean matchesOk = true;
    
    // for(Match<T> match: matches)
    // if(match.getSuccess() != Success.COMPLETE)
    // matchesOk = false;
    
    if(allMatched && matchesOk)
      success = Success.COMPLETE;
    else if(allMatched || matchesOk)
      success = Success.PARTIALLY;
    else
      success = Success.NONE;
  }
}
