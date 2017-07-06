package model.node;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;

public class NotTest extends NodeTestBase {

  private static final BoolNode NOT_A = new Not(A);

  @Test
  public void testEvaluate() throws Exception {
    evalute(NOT_A, FF, true);

    evalute(NOT_A, FT, true);

    evalute(NOT_A, TF, false);

    evalute(NOT_A, TT, false);
  }

  @Test
  public void testGetAsString() throws Exception {
    assertThat(NOT_A.getAsString(false), equalTo("NOT a"));
    assertThat(NOT_A.getAsString(true), equalTo("(NOT a)"));
  }

  @Test
  public void testGetUsedVariables() throws Exception {
    Set<Character> usedVars = NOT_A.getUsedVariables();
    assertThat(usedVars.size(), equalTo(1));
    assertThat(usedVars.iterator().next(), equalTo('a'));
  }

  @Test
  public void testNegate() throws Exception {
    assertThat(NOT_A.negate(), equalTo(A));
  }

}
