package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import play.data.DynamicForm;

public class TwoCompResultTest {

  @Test
  public void testParseFromFormCorrect() {
    DynamicForm form = mock(DynamicForm.class);

    when(form.get(TwoCompResult.VALUE)).thenReturn("-26");
    when(form.get(TwoCompResult.BINARY_ABS)).thenReturn("0001 1010");
    when(form.get(TwoCompResult.INVERTED_ABS)).thenReturn("1110 0101");
    when(form.get(StringConsts.FORM_VALUE)).thenReturn("1110 0110");

    TwoCompResult res = TwoCompResult.parseFromForm(form, true);
    assertThat(res.getBinaryAbs(), equalTo("0001 1010"));
    assertThat(res.getInvertedAbs(), equalTo("1110 0101"));

    NAryNumber learnerSolution = res.getLearnerSolution();
    assertThat(learnerSolution.getBase(), equalTo(NumberBase.BINARY));
    assertThat(learnerSolution.getValue(), equalTo(-26));

    NAryNumber targetNumber = res.getTargetNumber();
    assertThat(targetNumber.getBase(), equalTo(NumberBase.BINARY));
    assertThat(targetNumber.getValue(), equalTo(-26));

    assertThat(res.binaryAbsCorrect(), equalTo(true));
    assertThat(res.invertedAbsCorrect(), equalTo(true));
    assertThat(res.checkSolution(), equalTo(true));
  }

  @Test
  public void testParseFromFormWrong() {
    DynamicForm form = mock(DynamicForm.class);

    when(form.get(TwoCompResult.VALUE)).thenReturn("-26");
    when(form.get(TwoCompResult.BINARY_ABS)).thenReturn("0001 1110");
    when(form.get(TwoCompResult.INVERTED_ABS)).thenReturn("1010 0101");
    when(form.get(StringConsts.FORM_VALUE)).thenReturn("1111 1111");

    TwoCompResult res = TwoCompResult.parseFromForm(form, true);
    assertThat(res.getBinaryAbs(), equalTo("0001 1110"));
    assertThat(res.getInvertedAbs(), equalTo("1010 0101"));

    NAryNumber learnerSolution = res.getLearnerSolution();
    assertThat(learnerSolution.getBase(), equalTo(NumberBase.BINARY));
    assertThat(learnerSolution.getValue(), equalTo(-1));

    NAryNumber targetNumber = res.getTargetNumber();
    assertThat(targetNumber.getBase(), equalTo(NumberBase.BINARY));
    assertThat(targetNumber.getValue(), equalTo(-26));

    assertThat(res.binaryAbsCorrect(), equalTo(false));
    assertThat(res.invertedAbsCorrect(), equalTo(false));
    assertThat(res.checkSolution(), equalTo(false));
  }
  
}
