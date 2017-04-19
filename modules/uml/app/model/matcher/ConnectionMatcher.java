package model.matcher;

import model.UmlConnection;
import model.matching.Matcher;

public class ConnectionMatcher<T extends UmlConnection> extends Matcher<T> {

  public ConnectionMatcher() {
    super((conn1, conn2) -> {
      boolean pos1 = endsEqual(conn1.getStart(), conn2.getStart()) && endsEqual(conn1.getTarget(), conn2.getTarget());
      boolean pos2 = endsEqual(conn1.getStart(), conn2.getTarget()) && endsEqual(conn1.getTarget(), conn2.getStart());
      return pos1 || pos2;
    });
  }

  private static boolean endsEqual(String c1End, String c2End) {
    return c1End != null && c2End != null && c1End.equals(c2End);
  }

}
