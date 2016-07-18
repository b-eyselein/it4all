package model.bool.node;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.bool.BoolescheFunktionParser;
import model.bool.tree.Assignment;
import model.bool.tree.BoolescheFunktionTree;

public class AndTest {

  @Test
  public void testEvaluate() {
    BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("a and b");
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
