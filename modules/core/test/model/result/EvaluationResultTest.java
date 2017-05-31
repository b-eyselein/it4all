package model.result;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import model.exercise.Success;

public class EvaluationResultTest {

  @Test
  public void testCompletely() {
    EvaluationResult failure = new EvaluationResult(Success.FAILURE);
    assertThat(failure.getSuccess(), equalTo(Success.FAILURE));
    assertThat(failure.getPoints(), equalTo(0));

    EvaluationResult none = new EvaluationResult(Success.NONE);
    assertThat(none.getSuccess(), equalTo(Success.NONE));
    assertThat(none.getPoints(), equalTo(0));

    EvaluationResult partially = new EvaluationResult(Success.PARTIALLY);
    assertThat(partially.getSuccess(), equalTo(Success.PARTIALLY));
    assertThat(partially.getPoints(), equalTo(1));

    EvaluationResult complete = new EvaluationResult(Success.COMPLETE);
    assertThat(complete.getSuccess(), equalTo(Success.COMPLETE));
    assertThat(complete.getPoints(), equalTo(2));
  }

}
