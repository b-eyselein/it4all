package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class VariableTest extends NodeTestBase {

  private static final Variable A = new Variable('a');

  private static final Variable B = new Variable('b');

  @Test
  public void testEvaluate() throws CorrectionException {
    evalute(A, FF, false);
    evalute(B, FF, false);

    evalute(A, FT, false);
    evalute(B, FT, true);

    evalute(A, TF, true);
    evalute(B, TF, false);

    evalute(A, TT, true);
    evalute(B, TT, true);
  }

  @Test
  public void testGetAsString() {
    assertThat(A.getAsString(true), equalTo("a"));
    assertThat(A.getAsString(false), equalTo("a"));
  }

  @Test
  public void testGetVariable() {
    assertThat(A.getVariable(), equalTo('a'));
  }

  @Test
  public void testToString() {
    assertThat(A.toString(), equalTo("Variable(a)"));
  }
}
