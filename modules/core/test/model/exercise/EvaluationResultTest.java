package model.exercise;

import static org.junit.Assert.*;

import org.junit.Test;

import model.exercise.Success;

import model.exercise.EvaluationResult;

import static org.hamcrest.CoreMatchers.equalTo;

public class EvaluationResultTest {
  
  // FIXME: implement feedbackLevel!
  @Test
  public void testCompletely() {
    EvaluationResult result = new EvaluationResult(FeedbackLevel.NO_FEEDBACK, Success.NONE) {
      // FIXME: implement feedbackLevel!
      @Override
      public String getAsHtml() {
        return null;
      }
    };

    assertThat(result.getSuccess(), equalTo(Success.NONE));
    assertThat(result.getMinimalFeedbackLevel(), equalTo(FeedbackLevel.NO_FEEDBACK));
    assertThat(result.getPoints(), equalTo(0));

    result.setSuccess(Success.PARTIALLY);
    assertThat(result.getSuccess(), equalTo(Success.PARTIALLY));
    assertThat(result.getMinimalFeedbackLevel(), equalTo(FeedbackLevel.NO_FEEDBACK));
    assertThat(result.getPoints(), equalTo(1));

    result.setSuccess(Success.COMPLETE);
    assertThat(result.getSuccess(), equalTo(Success.COMPLETE));
    assertThat(result.getMinimalFeedbackLevel(), equalTo(FeedbackLevel.NO_FEEDBACK));
    assertThat(result.getPoints(), equalTo(2));

    result.setSuccess(Success.NONE);
    assertThat(result.getSuccess(), equalTo(Success.NONE));
    assertThat(result.getMinimalFeedbackLevel(), equalTo(FeedbackLevel.NO_FEEDBACK));
    assertThat(result.getPoints(), equalTo(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalSuccess() {
    EvaluationResult result = new EvaluationResult(FeedbackLevel.NO_FEEDBACK, Success.NONE) {
      // FIXME: implement feedbackLevel!
      @Override
      public String getAsHtml() {
        return null;
      }
    };

    result.setSuccess(null);
  }

}
