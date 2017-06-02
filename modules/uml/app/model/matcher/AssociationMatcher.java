package model.matcher;

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

  public static boolean endsParallelEqual(UmlAssociation assoc1, UmlAssociation assoc2) {
    return endsEqual(assoc1.getEnds().get(0), assoc2.getEnds().get(0))
        && endsEqual(assoc1.getEnds().get(1), assoc2.getEnds().get(1));
  }

  private static boolean endsEqual(UmlAssociationEnd c1End, UmlAssociationEnd c2End) {
    return c1End.getEndName().equals(c2End.getEndName());
  }

  @Override
  protected Match<UmlAssociation> instantiateMatch(UmlAssociation arg1, UmlAssociation arg2) {
    return new UmlAssociationMatch(arg1, arg2);
  }

}
