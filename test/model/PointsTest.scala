package model

import org.junit.Test
import model._
import org.junit.Assert._

import scala.language.postfixOps

class PointsTest {

  @Test
  def testCompare(): Unit = {
    assertTrue((1 halfPoint) > (0 points))
  }

}
