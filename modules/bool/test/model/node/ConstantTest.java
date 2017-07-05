package model.node;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import model.CorrectionException;

public class ConstantTest extends NodeTestBase {
  
  @Test
  public void testFalse() throws CorrectionException {
    evalute(Constant.FALSE, FF, false);
    
    evalute(Constant.FALSE, FT, false);
    
    evalute(Constant.FALSE, TF, false);
    
    evalute(Constant.FALSE, TT, false);
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
  public void testTrue() throws CorrectionException {
    evalute(Constant.TRUE, FF, true);
    
    evalute(Constant.TRUE, FT, true);
    
    evalute(Constant.TRUE, TF, true);
    
    evalute(Constant.TRUE, TT, true);
  }

}
