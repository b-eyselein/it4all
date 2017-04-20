package model.matcher;

import java.util.List;

import model.matching.Match;
import model.matching.Matcher;
import model.uml.UmlAssociation;
import model.uml.UmlAssociation.UmlAssociationEnd;

public class AssociationMatcher extends Matcher<UmlAssociation> {

  public AssociationMatcher() {
    super((assoc1, assoc2) -> endsParallelEqual(assoc1, assoc2) || endsCrossedEqual(assoc1, assoc2));
  }

  public static boolean endsCrossedEqual(UmlAssociation assoc1, UmlAssociation assoc2) {
    return endsEqual(assoc1.start, assoc2.target) && endsEqual(assoc1.target, assoc2.start);
  }

  public static boolean endsParallelEqual(UmlAssociation assoc1, UmlAssociation assoc2) {
    return endsEqual(assoc1.start, assoc2.start) && endsEqual(assoc1.target, assoc2.target);
  }

  private static boolean endsEqual(UmlAssociationEnd c1End, UmlAssociationEnd c2End) {
    return c1End.endName.equals(c2End.endName);
  }

  @Override
  protected void instantiateMatch(List<Match<UmlAssociation>> matches, UmlAssociation arg1, UmlAssociation arg2) {
    matches.add(new UmlAssociationMatch(arg1, arg2));
  }

}
