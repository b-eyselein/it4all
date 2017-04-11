package model.matcher;

import java.util.List;

import model.UmlConnection;
import model.matching.Matcher;

public class ConnectionMatcher extends Matcher<UmlConnection, UmlConnectionMatch, ConnectionMatchingResult> {

  public ConnectionMatcher() {
    super(
        // TODO: Test
        (conn1, conn2) -> conn1.getStart().equals(conn2.getStart()) && conn1.getTarget().equals(conn2.getTarget()),
        UmlConnectionMatch::new);
  }

  @Override
  protected ConnectionMatchingResult instantiateMatch(List<UmlConnectionMatch> matches, List<UmlConnection> theWrong,
      List<UmlConnection> theMissing) {
    return new ConnectionMatchingResult(matches, theWrong, theMissing);
  }

}
