package model.node;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import model.BooleanParsingException;
import model.BoolescheFunktionParser;
import model.tree.Assignment;
import model.tree.BoolFormula;

public class FalseTest {
  
  @Test
  public void testEvaluate() throws BooleanParsingException {
    BoolFormula t1 = BoolescheFunktionParser.parse("0");
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
