package model.points

import org.junit.Assert._
import org.junit.Test

class PointsTest {

  @Test
  def testCompare(): Unit = {
    assertTrue(singleHalfPoint > zeroPoints)
  }

}
