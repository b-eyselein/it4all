package model.node;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.BoolescheFunktionParser;
import model.tree.Assignment;
import model.tree.BoolescheFunktionTree;

public class VariableTest {
  
  @Test
  public void testEvaluate() {
    BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("a");
    Assignment assignment = new Assignment();
    
    assignment.setAssignment('a', false);
    assertFalse(t1.evaluate(assignment));
    
    assignment.setAssignment('a', true);
    assertTrue(t1.evaluate(assignment));
  }
  
  @Test
  public void testGetAsString() {
    assertThat((new Variable('a')).getAsString(true), equalTo("a"));
    assertThat((new Variable('a')).getAsString(false), equalTo("a"));
  }
  
  @Test
  public void testGetVariable() {
    Variable a = new Variable('a');
    assertThat(a.getVariable(), equalTo('a'));
  }
  
  @Test
  public void testToString() {
    assertThat((new Variable('a')).toString(), equalTo("a"));
  }
}
