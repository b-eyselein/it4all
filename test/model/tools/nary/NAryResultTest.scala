package model.tools.nary

import model.tools.nary.NumberBase._
import org.junit.Assert._
import org.junit.Test

class NAryResultTest {

  @Test
  def testCheckAddSolution(): Unit = {
    val resTrue = NAryAddResult(Octal, new NAryNumber(0, Octal), new NAryNumber(127, Octal), new NAryNumber(127, Octal))
    assertTrue(resTrue.solutionCorrect)

    val resFalse = NAryAddResult(Octal, new NAryNumber(2, Octal), new NAryNumber(127, Octal), new NAryNumber(127, Octal))
    assertFalse(resFalse.solutionCorrect)
  }

  @Test
  def testCheckConvSolution(): Unit = {
    val resTrue = NAryConvResult(new NAryNumber(8, Octal), Octal, Binary, new NAryNumber(8, Octal))
    assertTrue(resTrue.solutionCorrect)

    val resFalse = NAryConvResult(new NAryNumber(8, Octal), Octal, Binary, new NAryNumber(9, Octal))
    assertFalse(resFalse.solutionCorrect)
  }

}
