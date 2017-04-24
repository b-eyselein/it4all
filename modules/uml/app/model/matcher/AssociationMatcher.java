package model.matcher;

import java.util.List;

import model.matching.Match;
import model.matching.Matcher;
import model.uml.UmlAssociation;
import model.uml.UmlAssociationEnd;

public class AssociationMatcher extends Matcher<UmlAssociation> {

  public AssociationMatcher() {
    super((assoc1, assoc2) -> endsParallelEqual(assoc1, assoc2) || endsCrossedEqual(assoc1, assoc2));
  }

  public static boolean endsCrossedEqual(UmlAssociation assoc1, UmlAssociation assoc2) {
    return endsEqual(assoc1.getEnds().get(0), assoc2.getEnds().get(1))
        && endsEqual(assoc1.getEnds().get(1), assoc2.getEnds().get(0));
  }

  private static boolean endsEqual(UmlAssociationEnd c1End, UmlAssociationEnd c2End) {
    return c1End.getEndName().equals(c2End.getEndName());
  }

  public static boolean endsParallelEqual(UmlAssociation assoc1, UmlAssociation assoc2) {
    return endsEqual(assoc1.getEnds().get(0), assoc2.getEnds().get(0))
        && endsEqual(assoc1.getEnds().get(1), assoc2.getEnds().get(1));
  }

  @Override
  protected void instantiateMatch(List<Match<UmlAssociation>> matches, UmlAssociation arg1, UmlAssociation arg2) {
    matches.add(new UmlAssociationMatch(arg1, arg2));
  }

}
