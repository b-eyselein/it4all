package model;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;

public class EvaluationResultTest {
  
  @Test
  public void testCompletely() {
    EvaluationResult result = new EvaluationResult() {
    };
    assertThat(result.getSuccess(), equalTo(Success.NONE));
    assertThat(result.getPoints(), equalTo(0));

    result.setSuccess(Success.PARTIALLY);
    assertThat(result.getSuccess(), equalTo(Success.PARTIALLY));
    assertThat(result.getPoints(), equalTo(1));

    result.setSuccess(Success.COMPLETE);
    assertThat(result.getSuccess(), equalTo(Success.COMPLETE));
    assertThat(result.getPoints(), equalTo(2));

    result.setSuccess(Success.NONE);
    assertThat(result.getSuccess(), equalTo(Success.NONE));
    assertThat(result.getPoints(), equalTo(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalSuccess() {
    EvaluationResult result = new EvaluationResult() {
    };
    result.setSuccess(null);
  }

}
