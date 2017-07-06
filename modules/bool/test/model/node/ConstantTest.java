package model.node;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ConstantTest extends NodeTestBase {
  
  @Test
  public void testFalse() {
    evalute(Constant.FALSE, FF, false);
    
    evalute(Constant.FALSE, FT, false);
    
    evalute(Constant.FALSE, TF, false);
    
    evalute(Constant.FALSE, TT, false);

    BoolNode tru = Constant.FALSE.negate();

    assertNotNull(tru);

    evalute(tru, FF, true);
    
    evalute(tru, FT, true);
    
    evalute(tru, TF, true);
    
    evalute(tru, TT, true);
  }
  
  @Test
  public void testGetAsString() {
    assertThat(Constant.FALSE.getAsString(true), equalTo("0"));
    assertThat(Constant.FALSE.getAsString(false), equalTo("0"));
    
    assertThat(Constant.TRUE.getAsString(true), equalTo("1"));
    assertThat(Constant.TRUE.getAsString(false), equalTo("1"));
  }
  
  @Test
  public void testGetUsedVariables() {
    assertThat("Eine FALSE-Konstante sollte keine Variablen enthalten!", Constant.FALSE.getUsedVariables().size(),
        equalTo(0));
    assertThat("Eine TRUE-Konstante sollte keine Variablen enthalten!", Constant.TRUE.getUsedVariables().size(),
        equalTo(0));
  }
  
  @Test
  public void testToString() {
    assertThat(Constant.FALSE.toString(), equalTo("0"));
    assertThat(Constant.TRUE.toString(), equalTo("1"));
  }
  
  @Test
  public void testTrue() {
    evalute(Constant.TRUE, FF, true);
    
    evalute(Constant.TRUE, FT, true);
    
    evalute(Constant.TRUE, TF, true);
    
    evalute(Constant.TRUE, TT, true);
    
    BoolNode fal = Constant.TRUE.negate();

    evalute(fal, FF, false);
    
    evalute(fal, FT, false);
    
    evalute(fal, TF, false);
    
    evalute(fal, TT, false);
  }
  
}
