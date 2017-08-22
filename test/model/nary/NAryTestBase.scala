package model.nary

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat

object NAryTestBase {

  def verifyNumber(number: NAryNumber, expectedBase: NumberBase, expectedValue: Int) {
    assertNotNull(number)
    checkBase(number.base, expectedBase)
    assertThat(number.decimalValue, equalTo(expectedValue))
  }

  def checkBase(actual: NumberBase, expected: NumberBase) = assert(actual == expected, s"Awaited that number base is $expected but got $actual!")

}
