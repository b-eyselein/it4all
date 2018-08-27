package model.nary

import model.nary.NumberBase._
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class NAryResultTest {

  @Test
  def testCheckAddSolution(): Unit = {
    val resTrue = NAryAddResult(OCTAL, new NAryNumber(0, OCTAL), new NAryNumber(127, OCTAL), new NAryNumber(127, OCTAL))
    assertThat(resTrue.solutionCorrect, equalTo(true))

    val resFalse = NAryAddResult(OCTAL, new NAryNumber(2, OCTAL), new NAryNumber(127, OCTAL), new NAryNumber(127, OCTAL))
    assertThat(resFalse.solutionCorrect, equalTo(false))
  }

  @Test
  def testCheckConvSolution(): Unit = {
    val resTrue = NAryConvResult(new NAryNumber(8, OCTAL), OCTAL, BINARY, new NAryNumber(8, OCTAL))
    assertThat(resTrue.solutionCorrect, equalTo(true))

    val resFalse = NAryConvResult(new NAryNumber(8, OCTAL), OCTAL, BINARY, new NAryNumber(9, OCTAL))
    assertThat(resFalse.solutionCorrect, equalTo(false))
  }

}