package model.bool.node;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.bool.BoolescheFunktionParser;
import model.bool.tree.Assignment;
import model.bool.tree.BoolescheFunktionTree;

public class OrTest {
  
  @Test
  public void testEvaluate() {
    BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("a or b");
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
    assertTrue(t1.evaluate(assignment));
    
    // a = 0, b = 1
    assignment.setAssignment('a', false);
    assertTrue(t1.evaluate(assignment));
  }
  
}
