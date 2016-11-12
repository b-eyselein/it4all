package model.node;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.BoolescheFunktionParser;
import model.tree.Assignment;
import model.tree.BoolescheFunktionTree;

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

  @Test
  public void testGetAsString() {
    assertThat((new Not(new Variable('a'))).getAsString(true), equalTo("NOT a"));
    assertThat((new Not(new Variable('a'))).getAsString(true), equalTo("NOT a"));
  }
}
