package model.boolscheAlgebraTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.boolescheAlgebra.BoolescheFunktionParser;
import model.boolescheAlgebra.BFTree.Assignment;
import model.boolescheAlgebra.BFTree.BoolescheFunktionTree;

public class BFP_Test_AND {
  private static final char[] ab = {'a', 'b'};

  @Test
  public void test() {
    BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("a and b", ab);
    Assignment assignment = new Assignment();

    assignment.setAssignment('a', false);
    assignment.setAssignment('b', false);
    assertFalse(t1.evaluate(assignment));

    assignment.setAssignment('a', true);
    assertFalse(t1.evaluate(assignment));

    assignment.setAssignment('b', true);
    assertTrue(t1.evaluate(assignment));

    assignment.setAssignment('a', false);
    assertFalse(t1.evaluate(assignment));
  }

}
