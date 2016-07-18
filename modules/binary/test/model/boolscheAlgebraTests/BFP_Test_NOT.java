package model.boolscheAlgebraTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.boolescheAlgebra.BoolescheFunktionParser;
import model.boolescheAlgebra.BFTree.Assignment;
import model.boolescheAlgebra.BFTree.BoolescheFunktionTree;

public class BFP_Test_NOT {
  
  private static final char[] a = {'a'};
  
  @Test
  public void test_not_0() {
    BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("not a", a);
    Assignment assignment = new Assignment();
    
    assignment.setAssignment('a', false);
    assertTrue(t1.evaluate(assignment));
    
    assignment.setAssignment('a', true);
    assertFalse(t1.evaluate(assignment));
  }
  
}
