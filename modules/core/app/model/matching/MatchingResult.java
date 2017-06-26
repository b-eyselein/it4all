package model.matching;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.exercise.Success;
import model.result.EvaluationResult;

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

  public static <T, M extends Match<T>> MatchingResult<T, M> merge(MatchingResult<T, M> res1,
      MatchingResult<T, M> res2) {

    if(!res1.getMatchName().equals(res2.getMatchName()))
      return null;

    List<M> matches = Stream.concat(res1.getMatches().stream(), res2.getMatches().stream())
        .collect(Collectors.toList());

    return new MatchingResult<>(res1.getMatchName(), matches);
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

  public List<M> getMatches() {
    return matches;
  }

  public String getMatchName() {
    return matchName;
  }

}
