package model.bool.node;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import model.bool.BoolescheFunktionParser;
import model.bool.tree.Assignment;
import model.bool.tree.BoolescheFunktionTree;

public class FalseTest {

  @Test
  public void testEvaluate() {
    BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("0");
    Assignment assignment = new Assignment();

    assignment.setAssignment('a', false);
    assertFalse(t1.evaluate(assignment));

    assignment.setAssignment('a', true);
    assertFalse(t1.evaluate(assignment));
  }

}
