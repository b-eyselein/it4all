package model

import enumeratum.{EnumEntry, PlayEnum}
import model.tools.Helper.UntypedExercise

import scala.concurrent.Future

sealed abstract class Level(val level: Int) extends EnumEntry {

  def pointsForExerciseCompletion: Int = Math.pow(2.toDouble, level.toDouble - 1).toInt

}

object Level extends PlayEnum[Level] {

  val values: IndexedSeq[Level] = findValues

  case object Beginner     extends Level(1)
  case object Intermediate extends Level(2)
  case object Advanced     extends Level(3)
  case object Expert       extends Level(4)

}

final case class LevelForExercise(
  exerciseId: Int,
  collectionId: Int,
  level: Level
)

final case class UserProficiency(
  username: String,
  topic: Topic,
  pointsForExercises: Seq[LevelForExercise] = Seq.empty
) {

  lazy val getPoints: Int = pointsForExercises.map { _.level.pointsForExerciseCompletion }.sum

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

trait ProficiencyRepository {
  self: play.api.db.slick.HasDatabaseConfig[slick.jdbc.JdbcProfile] =>

  import MyPostgresProfile.api._

  private val proficienciesTQ = TableQuery[ProficienciesTable]

  def userProficienciesForTool(username: String, toolId: String): Future[Seq[UserProficiency]] = ???
  // db.run(proficienciesTQ.filter { p => p.username === username && p.toolId === toolId }.result)

  def updateUserProficiency(username: String, exercise: UntypedExercise, topicWithLevel: TopicWithLevel): Future[Boolean] = ???

  private class ProficienciesTable(tag: Tag) extends Table[UserProficiency](tag, "user_proficiencies") {

    // Primary key cols

    def username = column[String]("username")

    def topicJson = column[Topic]("topic_json")

    def pointsForExercises = column[Seq[LevelForExercise]]("level_for_exercises")

    override def * = (username, topicJson, pointsForExercises) <> (UserProficiency.tupled, UserProficiency.unapply)

  }

}
