package model

import org.junit.Test

import ScalaUtils._

class ScalaUtilsTest {

  @Test
  def testKgv() {
    assert(kgv(1, 2) == 2)
    assert(kgv(3, 7) == 21)
    assert(kgv(4, 16) == 16)
    assert(kgv(3, 77) == 231)
  }

  @Test
  def testGgt() {
    assert(ggt(1, 1) == 1)
    assert(ggt(5, 7) == 1)
    assert(ggt(2, 16) == 16)
    assert(ggt(9, 3) == 3)
    assert(ggt(44, 77) == 11)
  }

}
