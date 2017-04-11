package model.matcher;

import java.util.List;

import model.UmlConnection;
import model.matching.MatchingResult;

public class ConnectionMatchingResult extends MatchingResult<UmlConnection, UmlConnectionMatch> {

  public ConnectionMatchingResult(List<UmlConnectionMatch> theMatches, List<UmlConnection> theWrong,
      List<UmlConnection> theMissing) {
    super(theMatches, theWrong, theMissing);
  }

}
