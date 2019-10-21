package model.tools.nary

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.{assertNotNull, assertThat}

object NAryTestBase {

  def verifyNumber(number: NAryNumber, expectedBase: NumberBase, expectedValue: Int): Unit = {
    assertNotNull(number)
    checkBase(number.base, expectedBase)
    assertThat(number.decimalValue, equalTo(expectedValue))
  }

  def checkBase(actual: NumberBase, expected: NumberBase): Unit = {
    assert(actual == expected, s"Awaited that number base is $expected but got $actual!")
  }

}
