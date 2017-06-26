package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NAryResultTest {
  
  @Test
  public void test() {
    NAryResult res = new NAryResult(new NAryNumber(NumberBase.OCTAL)) {

      @Override
      public boolean checkSolution() {
        return false;
      }

    };
    assertThat(res.getTargetNumber().getBase(), equalTo(NumberBase.OCTAL));
    assertThat(res.getTargetNumber().getValue(), equalTo(0));
    assertThat(res.checkSolution(), equalTo(false));
  }
  
}
