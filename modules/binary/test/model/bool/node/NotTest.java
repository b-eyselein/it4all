package model.bool.node;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.bool.BoolescheFunktionParser;
import model.bool.tree.Assignment;
import model.bool.tree.BoolescheFunktionTree;

public class NotTest {
  
  @Test
  public void testEvaluate() {
    BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("not a");
    Assignment assignment = new Assignment();
    
    assignment.setAssignment('a', false);
    assertTrue(t1.evaluate(assignment));
    
    assignment.setAssignment('a', true);
    assertFalse(t1.evaluate(assignment));
  }
  
}
