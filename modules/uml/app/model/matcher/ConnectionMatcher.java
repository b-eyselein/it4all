package model.matcher;

import java.util.List;

import model.UmlConnection;
import model.matching.Matcher;
import model.matching.MatchingResult;

public class ConnectionMatcher extends Matcher<UmlConnection, UmlConnectionMatch> {
  
  public ConnectionMatcher() {
    super(
        // TODO: Test
        (conn1, conn2) -> conn1.getStart().equals(conn2.getStart()) && conn1.getTarget().equals(conn2.getTarget()),
        UmlConnectionMatch::new);
  }
  
  @Override
  protected MatchingResult<UmlConnection, UmlConnectionMatch> instantiateMatch(List<UmlConnectionMatch> matches,
      List<UmlConnection> theWrong, List<UmlConnection> theMissing) {
    return new MatchingResult<>(matches, theWrong, theMissing);
  }
  
}
