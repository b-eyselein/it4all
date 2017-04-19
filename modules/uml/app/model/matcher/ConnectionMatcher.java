package model.matcher;

import model.UmlConnection;
import model.matching.Matcher;

public class ConnectionMatcher<T extends UmlConnection> extends Matcher<T> {

  public ConnectionMatcher() {
    super((conn1, conn2) -> {
      String c1Start = conn1.getStart();
      String c2Start = conn2.getStart();

      String c1Target = conn1.getTarget();
      String c2Target = conn2.getTarget();

      System.out.println("(" + c1Start + " -> " + c1Target + ") :: (" + c2Start + " -> " + c2Target + ")");

      return endsEqual(c1Start, c2Start) && endsEqual(c1Target, c2Target);
    });
  }

  private static boolean endsEqual(String c1End, String c2End) {
    return c1End != null && c2End != null && c1End.equals(c2End);
  }

}
