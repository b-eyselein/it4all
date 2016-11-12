package model.node;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import model.BoolescheFunktionParser;
import model.tree.Assignment;
import model.tree.BoolescheFunktionTree;

public class TrueTest {
  
  @Test
  public void testEvaluate() {
    BoolescheFunktionTree t1 = BoolescheFunktionParser.parse("1");
    Assignment assignment = new Assignment();
    
    assignment.setAssignment('a', false);
    assertTrue(t1.evaluate(assignment));
    
    assignment.setAssignment('a', true);
    assertTrue(t1.evaluate(assignment));
  }
  
  @Test
  public void testGetAsString() {
    assertThat((new True()).getAsString(true), equalTo("1"));
    assertThat((new True()).getAsString(false), equalTo("1"));
  }
  
  @Test
  public void testToString() {
    assertThat((new True()).toString(), equalTo("1"));
  }
  
}
