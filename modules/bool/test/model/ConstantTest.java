package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ConstantTest extends NodeTestBase {
  
  private static final ScalaNode TRUE = model.TRUE$.MODULE$;
  private static final ScalaNode FALSE = model.FALSE$.MODULE$;

  @Test
  public void testFalse() {
    evalute(FALSE, FF, false);
    
    evalute(FALSE, FT, false);
    
    evalute(FALSE, TF, false);
    
    evalute(FALSE, TT, false);

    ScalaNode tru = FALSE.negate();

    System.out.println(tru);

    assertNotNull(tru);

    evalute(tru, FF, true);
    
    evalute(tru, FT, true);
    
    evalute(tru, TF, true);
    
    evalute(tru, TT, true);
  }
  
  @Test
  public void testGetAsString() {
    assertThat(FALSE.getAsString(true), equalTo("0"));
    assertThat(FALSE.getAsString(false), equalTo("0"));
    
    assertThat(TRUE.getAsString(true), equalTo("1"));
    assertThat(TRUE.getAsString(false), equalTo("1"));
  }
  
  @Test
  public void testGetUsedVariables() {
    assertThat("Eine FALSE-Konstante sollte keine Variablen enthalten!", FALSE.getUsedVariables().size(), equalTo(0));
    assertThat("Eine TRUE-Konstante sollte keine Variablen enthalten!", TRUE.getUsedVariables().size(), equalTo(0));
  }
  
  @Test
  public void testToString() {
    assertThat(FALSE.toString(), equalTo("Constant(false)"));
    assertThat(TRUE.toString(), equalTo("Constant(true)"));
  }
  
  @Test
  public void testTrue() {
    evalute(TRUE, FF, true);
    
    evalute(TRUE, FT, true);
    
    evalute(TRUE, TF, true);
    
    evalute(TRUE, TT, true);
    
    ScalaNode fal = TRUE.negate();

    evalute(fal, FF, false);
    
    evalute(fal, FT, false);
    
    evalute(fal, TF, false);
    
    evalute(fal, TT, false);
  }
  
}
