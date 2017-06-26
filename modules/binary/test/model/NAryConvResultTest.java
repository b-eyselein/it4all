package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import play.data.DynamicForm;

public class NAryConvResultTest {

  @Test
  public void testCheckSolution() {
    NAryConvResult resTrue = new NAryConvResult(new NAryNumber(8, NumberBase.OCTAL), NumberBase.OCTAL,
        NumberBase.BINARY, new NAryNumber(8, NumberBase.OCTAL));
    assertThat(resTrue.checkSolution(), equalTo(true));

    NAryConvResult resFalse = new NAryConvResult(new NAryNumber(8, NumberBase.OCTAL), NumberBase.OCTAL,
        NumberBase.BINARY, new NAryNumber(9, NumberBase.OCTAL));
    assertThat(resFalse.checkSolution(), equalTo(false));
  }

  @Test
  public void testParseFromForm() {
    DynamicForm form = mock(DynamicForm.class);
    when(form.get(NAryConvResult.STARTING_NB)).thenReturn(NumberBase.BINARY.toString());
    when(form.get(NAryConvResult.TARGET_NB)).thenReturn(NumberBase.OCTAL.toString());
    when(form.get(NAryConvResult.VALUE)).thenReturn("0000 1111");

    // value is reversed!!
    when(form.get(StringConsts.FORM_VALUE)).thenReturn("17");

    NAryConvResult res = NAryConvResult.parseFromForm(form);

    assertThat(res.getFromNumberBase(), equalTo(NumberBase.BINARY));
    assertThat(res.getToNumberBase(), equalTo(NumberBase.OCTAL));

    NAryNumber fromValue = res.getFromValue();
    assertNotNull(fromValue);
    assertThat(fromValue.getBase(), equalTo(NumberBase.BINARY));
    assertThat(fromValue.getValue(), equalTo(15));

    NAryNumber targetNumber = res.getTargetNumber();
    assertNotNull(targetNumber);
    assertThat(targetNumber.getBase(), equalTo(NumberBase.OCTAL));
    assertThat(targetNumber.getValue(), equalTo(15));

    NAryNumber learnerSolution = res.getLearnerSolution();
    assertNotNull(learnerSolution);
    assertThat(learnerSolution.getBase(), equalTo(NumberBase.OCTAL));
    assertThat(learnerSolution.getValue(), equalTo(15));
  }
}
