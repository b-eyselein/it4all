package model

import model.tools.Topic

object UserProficiency {

  def getAndRemove[T, V](key: T, from: Map[T, V]): (Option[V], Map[T, V]) = {

    @scala.annotation.tailrec
    def go(remainingMap: List[(T, V)], checkedMap: Seq[(T, V)]): (Option[V], Map[T, V]) = remainingMap match {
      case Nil => (None, checkedMap.toMap)
      case (headKey, headValues) :: tail =>
        if (headKey == key) {
          (Some(headValues), (checkedMap ++ tail).toMap)
        } else {
          go(tail, checkedMap :+ ((headKey, headValues)))
        }
    }

    go(from.toList, Seq.empty)
  }

}

final case class UserProficiency(
  username: String,
  topic: Topic,
  pointsForLevels: Map[Int, Int]
) {

  type Level = Int

  def getLevel: Level = {

    @annotation.tailrec
    def checkLevel(currentLevel: Int, remainingPoints: Int): Int = {
      val pointsNeeded = 10 * currentLevel

      if (remainingPoints >= pointsNeeded) {
        checkLevel(currentLevel + 1, remainingPoints - pointsNeeded)
      } else {
        currentLevel
      }
    }

    val completePoints = (0 until topic.maxLevel).map { reachableLevel =>
      Math.min(
        10 * reachableLevel,                         // max reachable points
        pointsForLevels.getOrElse(reachableLevel, 0) // points gotten for level
      )
    }.sum

    checkLevel(0, completePoints)
  }

  def explainLevel: Map[Level, Map[Level, Int]] = {

    @scala.annotation.tailrec
    def go(
      currentLevel: Int,
      pointsForCurrentLevel: Int,
      remainingPoints: List[(Level, Int)],
      acc: Map[Level, Map[Level, Int]]
    ): Map[Level, Map[Int, Int]] = remainingPoints match {
      case Nil => acc
      case (headLevel, headPoints) :: tail =>
        val neededPoints  = 10 * currentLevel
        val currentPoints = pointsForCurrentLevel + headPoints

        if (currentPoints > neededPoints) {
          val newPointsForHeadLevel = currentPoints - neededPoints

          go(
            currentLevel + 1,
            0,
            (headLevel -> newPointsForHeadLevel) +: tail,
            acc
          )
        } else {
          go(
            currentLevel + 1,
            pointsForCurrentLevel + headPoints,
            tail,
            acc + (currentLevel -> Map(currentLevel -> 0))
          )
        }
    }

    go(1, 0, pointsForLevels.toList.sortBy(_._1), Map.empty)
  }

}
