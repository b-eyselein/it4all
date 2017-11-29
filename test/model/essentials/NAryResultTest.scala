package model.essentials

import model.core.StringConsts._
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import model.essentials.NumberBase._
import org.mockito.Mockito.{mock, when}
import play.data.DynamicForm

class NAryResultTest {

  @Test
  def testCheckAddSolution() {
    val resTrue = new NAryAddResult(OCTAL, new NAryNumber(0, OCTAL), new NAryNumber(127, OCTAL), new NAryNumber(127, OCTAL))
    assertThat(resTrue.solutionCorrect, equalTo(true))

    val resFalse = new NAryAddResult(OCTAL, new NAryNumber(2, OCTAL), new NAryNumber(127, OCTAL), new NAryNumber(127, OCTAL))
    assertThat(resFalse.solutionCorrect, equalTo(false))
  }

  @Test
  def testParseTwoCompFromFormCorrect() {
    val form: DynamicForm = mock(classOf[DynamicForm])

    when(form.get(NAryResult.VALUE_NAME)).thenReturn("-26")
    when(form.get(NAryResult.BINARY_ABS)).thenReturn("0001 1010")
    when(form.get(NAryResult.INVERTED_ABS)).thenReturn("1110 0101")
    when(form.get(FORM_VALUE)).thenReturn("1110 0110")

    val res: TwoCompResult = TwoCompResult.parseFromForm(form, isVerbose = true)
    assertThat(res.binaryAbs, equalTo("0001 1010"))
    assertThat(res.invertedAbs, equalTo("1110 0101"))

    NAryTestBase.verifyNumber(res.learnerSolution, BINARY, -26)
    NAryTestBase.verifyNumber(res.targetNumber, BINARY, -26)

    assertThat(res.binaryAbsCorrect, equalTo(true))
    assertThat(res.invertedAbsCorrect, equalTo(true))
  }

  @Test
  def testParseTwoCompFromFormWrong() {
    val form: DynamicForm = mock(classOf[DynamicForm])

    when(form.get(NAryResult.VALUE_NAME)).thenReturn("-26")
    when(form.get(NAryResult.BINARY_ABS)).thenReturn("0001 1110")
    when(form.get(NAryResult.INVERTED_ABS)).thenReturn("1010 0101")
    when(form.get(FORM_VALUE)).thenReturn("1111 1111")

    val res = TwoCompResult.parseFromForm(form, isVerbose = true)
    assertThat(res.binaryAbs, equalTo("0001 1110"))
    assertThat(res.invertedAbs, equalTo("1010 0101"))

    NAryTestBase.verifyNumber(res.learnerSolution, BINARY, -1)
    NAryTestBase.verifyNumber(res.targetNumber, BINARY, -26)

    assertThat(res.binaryAbsCorrect, equalTo(false))
    assertThat(res.invertedAbsCorrect, equalTo(false))
    assertThat(res.checkSolution, equalTo(false))
  }

  @Test
  def testParseAddFromForm() {
    val form = mock(classOf[DynamicForm])

    when(form.get(NAryResult.SUMMAND_1)).thenReturn("0000 1001")
    when(form.get(NAryResult.SUMMAND_2)).thenReturn("0000 0110")
    when(form.get(NAryResult.BASE_NAME)).thenReturn(BINARY.toString)
    // value is reversed!!
    when(form.get(FORM_VALUE)).thenReturn("1111 0000")

    val res = NAryAddResult.parseFromForm(form)

    NAryTestBase.checkBase(res.base, BINARY)

    NAryTestBase.verifyNumber(res.firstSummand, BINARY, 9)

    NAryTestBase.verifyNumber(res.secondSummand, BINARY, 6)

    NAryTestBase.verifyNumber(res.learnerSolution, BINARY, 15)
  }

  @Test
  def testCheckConvSolution() {
    val resTrue = new NAryConvResult(new NAryNumber(8, OCTAL), OCTAL, BINARY, new NAryNumber(8, OCTAL))
    assertThat(resTrue.solutionCorrect, equalTo(true))

    val resFalse = new NAryConvResult(new NAryNumber(8, OCTAL), OCTAL, BINARY, new NAryNumber(9, OCTAL))
    assertThat(resFalse.solutionCorrect, equalTo(false))
  }

  @Test
  def testParseConvFromFormCorrect() {
    val form = mock(classOf[DynamicForm])
    when(form.get(NAryResult.STARTING_NB)).thenReturn(BINARY.toString)
    when(form.get(NAryResult.TARGET_NB)).thenReturn(OCTAL.toString)
    when(form.get(NAryResult.VALUE_NAME)).thenReturn("0000 1111")

    // value is reversed!!
    when(form.get(FORM_VALUE)).thenReturn("17")

    val res = NAryConvResult.parseFromForm(form)

    NAryTestBase.checkBase(res.startingBase, BINARY)
    NAryTestBase.checkBase(res.targetBase, OCTAL)

    NAryTestBase.verifyNumber(res.startingValue, BINARY, 15)
    NAryTestBase.verifyNumber(res.targetNumber, OCTAL, 15)
    NAryTestBase.verifyNumber(res.learnerSolution, OCTAL, 15)
  }
}