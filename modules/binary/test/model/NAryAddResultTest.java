package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import play.data.DynamicForm;

public class NAryAddResultTest {
  
  @Test
  public void testCheckSolution() {
    NAryAddResult resTrue = new NAryAddResult(NumberBase.OCTAL, new NAryNumber(NumberBase.OCTAL),
        new NAryNumber(127, NumberBase.OCTAL), new NAryNumber(127, NumberBase.OCTAL));
    assertThat(resTrue.checkSolution(), equalTo(true));
    
    NAryAddResult resFalse = new NAryAddResult(NumberBase.OCTAL, new NAryNumber(2, NumberBase.OCTAL),
        new NAryNumber(127, NumberBase.OCTAL), new NAryNumber(127, NumberBase.OCTAL));
    assertThat(resFalse.checkSolution(), equalTo(false));
  }
  
  @Test
  public void testParseFromForm() {
    DynamicForm form = mock(DynamicForm.class);
    when(form.get(NAryAddResult.SUMMAND_1)).thenReturn("0000 1001");
    when(form.get(NAryAddResult.SUMMAND_2)).thenReturn("0000 0110");
    when(form.get(NAryAddResult.BASE_NAME)).thenReturn(NumberBase.BINARY.toString());
    
    // value is reversed!!
    when(form.get(StringConsts.FORM_VALUE)).thenReturn("1111 0000");
    
    NAryAddResult res = NAryAddResult.parseFromForm(form);
    
    assertThat(res.getBase(), equalTo(NumberBase.BINARY));
    
    NAryNumber firstSummand = res.getFirstSummand();
    assertNotNull(firstSummand);
    assertThat(firstSummand.getBase(), equalTo(NumberBase.BINARY));
    assertThat(firstSummand.getValue(), equalTo(9));
    
    NAryNumber secondSummand = res.getSecondSummand();
    assertNotNull(secondSummand);
    assertThat(secondSummand.getBase(), equalTo(NumberBase.BINARY));
    assertThat(secondSummand.getValue(), equalTo(6));
    
    NAryNumber learnerSolution = res.getLearnerSolution();
    assertNotNull(learnerSolution);
    assertThat(learnerSolution.getBase(), equalTo(NumberBase.BINARY));
    assertThat(learnerSolution.getValue(), equalTo(15));
  }
  
}
