package model.matcher;

import model.UmlConnection;
import model.matching.Matcher;

public class ConnectionMatcher<T extends UmlConnection> extends Matcher<T> {

  public ConnectionMatcher() {
    super((conn1, conn2) -> conn1.getStart().equals(conn2.getStart()) && conn1.getTarget().equals(conn2.getTarget()));
  }

}
