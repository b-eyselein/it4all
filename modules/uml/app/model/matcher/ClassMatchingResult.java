package model.matcher;

import java.util.List;

import model.UmlClass;
import model.matching.MatchingResult;

public class ClassMatchingResult extends MatchingResult<UmlClass, UmlClassMatch> {

  public ClassMatchingResult(List<UmlClassMatch> theMatches, List<UmlClass> theWrong, List<UmlClass> theMissing) {
    super(theMatches, theWrong, theMissing);
  }

}
