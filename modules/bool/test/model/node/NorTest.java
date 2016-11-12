package model.node;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.BoolescheFunktionParser;
import model.tree.Assignment;
import model.tree.BoolescheFunktionTree;

public class NorTest {

  @Test
  public void testEvaluate() {
    BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("a nor b");
    Assignment assignment = new Assignment();

    // a = 0, b = 0
    assignment.setAssignment('a', false);
    assignment.setAssignment('b', false);
    assertTrue(t1.evaluate(assignment));

    // a = 1, b = 0
    assignment.setAssignment('a', true);
    assertFalse(t1.evaluate(assignment));

    // a = 1, b = 1
    assignment.setAssignment('b', true);
    assertFalse(t1.evaluate(assignment));

    // a = 0, b = 1
    assignment.setAssignment('a', false);
    assertFalse(t1.evaluate(assignment));
  }

}
