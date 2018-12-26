package model.nary

import model.nary.NumberBase._
import org.junit.Assert._
import org.junit.Test

class NAryResultTest {

  @Test
  def testCheckAddSolution(): Unit = {
    val resTrue = NAryAddResult(OCTAL, new NAryNumber(0, OCTAL), new NAryNumber(127, OCTAL), new NAryNumber(127, OCTAL))
    assertTrue(resTrue.solutionCorrect)

    val resFalse = NAryAddResult(OCTAL, new NAryNumber(2, OCTAL), new NAryNumber(127, OCTAL), new NAryNumber(127, OCTAL))
    assertFalse(resFalse.solutionCorrect)
  }

  @Test
  def testCheckConvSolution(): Unit = {
    val resTrue = NAryConvResult(new NAryNumber(8, OCTAL), OCTAL, BINARY, new NAryNumber(8, OCTAL))
    assertTrue(resTrue.solutionCorrect)

    val resFalse = NAryConvResult(new NAryNumber(8, OCTAL), OCTAL, BINARY, new NAryNumber(9, OCTAL))
    assertFalse(resFalse.solutionCorrect)
  }

}