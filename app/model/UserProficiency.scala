package model

import enumeratum.{EnumEntry, PlayEnum}

sealed abstract class Level(val level: Int) extends EnumEntry

object Level extends PlayEnum[Level] {

  val values: IndexedSeq[Level] = findValues

  case object Beginner extends Level(1)

  case object Intermediate extends Level(2)

  case object Advanced extends Level(3)

  case object Expert extends Level(4)

  val ordering: Ordering[Level] = (lvl1, lvl2) => lvl1.level - lvl2.level

}

object UserProficiency {

  def pointsGainedForCompletion(level: Level): Int = Math.pow(2, level.level).toInt

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
  beginnerPoints: Int = 0,
  intermediatePoints: Int = 0,
  advancedPoints: Int = 0,
  expertPoints: Int = 0
) {

  def pointsForLevels: Map[Level, Int] = Map(
    Level.Beginner     -> beginnerPoints,
    Level.Intermediate -> intermediatePoints,
    Level.Advanced     -> advancedPoints,
    Level.Expert       -> expertPoints
  )

  def withUpdatedPointsForLevel(level: Level, points: Int): UserProficiency = level match {
    case Level.Beginner     => this.copy(beginnerPoints = beginnerPoints + points)
    case Level.Intermediate => this.copy(intermediatePoints = intermediatePoints + points)
    case Level.Advanced     => this.copy(advancedPoints = advancedPoints + points)
    case Level.Expert       => this.copy(expertPoints = expertPoints + points)
  }

  def getLevel: Level = {

    @annotation.tailrec
    def checkLevels(remainingLevels: List[Level], remainingPoints: Int): Level = remainingLevels match {
      case Nil => ???
      case currentLevel :: tail =>
        val pointsNeeded = 10 * currentLevel.level

        if (remainingPoints >= pointsNeeded) {
          checkLevels(tail, remainingPoints - pointsNeeded)
        } else {
          currentLevel
        }
    }

    val completePoints = Level.values.map { reachableLevel =>
      Math.min(
        10 * reachableLevel.level,                   // max reachable points
        pointsForLevels.getOrElse(reachableLevel, 0) // points gotten for level
      )
    }.sum

    checkLevels(Level.values.toList, completePoints)
  }

  def explainLevel: Map[Level, Map[Level, Int]] = {

    implicit val levelOrdering: Ordering[Level] = Level.ordering

    @scala.annotation.tailrec
    def go(
      remainingLevels: List[Level],
      pointsForCurrentLevel: Int,
      remainingPoints: List[(Level, Int)],
      acc: Map[Level, Map[Level, Int]]
    ): Map[Level, Map[Level, Int]] = remainingLevels match {
      case Nil => ???
      case currentLevel :: levelsTail =>
        remainingPoints match {
          case Nil => acc
          case (headLevel, headPoints) :: pointsTail =>
            val neededPoints  = 10 * currentLevel.level
            val currentPoints = pointsForCurrentLevel + headPoints

            if (currentPoints > neededPoints) {
              val newPointsForHeadLevel = currentPoints - neededPoints

              go(
                levelsTail,
                0,
                (headLevel -> newPointsForHeadLevel) +: pointsTail,
                acc
              )
            } else {
              go(
                levelsTail,
                pointsForCurrentLevel + headPoints,
                pointsTail,
                acc + (currentLevel -> Map(currentLevel -> 0))
              )
            }
        }
    }

    go(Level.values.toList, 0, pointsForLevels.toList.sortBy(_._1), Map.empty)
  }

}
