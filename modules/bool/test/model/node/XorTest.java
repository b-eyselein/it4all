package model.node;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.BoolescheFunktionParser;
import model.tree.Assignment;
import model.tree.BoolescheFunktionTree;

public class XorTest {
  
  @Test
  public void testEvaluate() {
    BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("a xor b");
    Assignment assignment = new Assignment();
    
    // a = 0, b = 0
    assignment.setAssignment('a', false);
    assignment.setAssignment('b', false);
    assertFalse(t1.evaluate(assignment));

    // a = 1, b = 0
    assignment.setAssignment('a', true);
    assertTrue(t1.evaluate(assignment));

    // a = 1, b = 1
    assignment.setAssignment('b', true);
    assertFalse(t1.evaluate(assignment));

    // a = 0, b = 1
    assignment.setAssignment('a', false);
    assertTrue(t1.evaluate(assignment));
  }
  
}
