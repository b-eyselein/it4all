package model

import enumeratum.{EnumEntry, PlayEnum}

sealed abstract class Level(val level: Int) extends EnumEntry {

  def pointsForExerciseCompletion: Int = Math.pow(2, level - 1).toInt

  def pointsNeededForLevelCompletion: Int = 10 * level

}

object Level extends PlayEnum[Level] {

  val values: IndexedSeq[Level] = findValues

  case object Beginner extends Level(1)

  case object Intermediate extends Level(2)

  case object Advanced extends Level(3)

  case object Expert extends Level(4)

  val ordering: Ordering[Level] = (lvl1, lvl2) => lvl1.level - lvl2.level

}

final case class LevelForExercise(
  exerciseId: Int,
  collectionId: Int,
  level: Level
)

final case class UserProficiency(
  username: String,
  topic: Topic,
  pointsForExercises: Set[LevelForExercise] = Set.empty
) {

  lazy val getPoints: Int = pointsForExercises.toSeq.map { _.level.pointsForExerciseCompletion }.sum

  def pointsForNextLevel: Int = getPoints match {
    case e if e >= 60 => Int.MaxValue
    case a if a >= 30 => 60
    case i if i >= 10 => 30
    case _            => 10
  }

  def getLevel: Level = getPoints match {
    case e if e >= 60 => Level.Expert
    case a if a >= 30 => Level.Advanced
    case i if i > 10  => Level.Intermediate
    case _            => Level.Beginner
  }

}
