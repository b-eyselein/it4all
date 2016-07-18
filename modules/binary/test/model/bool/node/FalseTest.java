package model.bool.node;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

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
  
  @Test
  public void testGetAsString() {
    assertThat((new False()).getAsString(true), equalTo("0"));
    assertThat((new False()).getAsString(false), equalTo("0"));
  }
  
  @Test
  public void testToString() {
    assertThat((new False()).toString(), equalTo("0"));
  }
}
