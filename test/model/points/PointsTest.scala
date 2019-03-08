package model.points

import org.junit.Assert._
import org.junit.Test

import scala.language.postfixOps

class PointsTest {

  @Test
  def testCompare(): Unit = {
    assertTrue((1 halfPoint) > (zeroPoints))
  }

}
