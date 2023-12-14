package model

import model.graphql.GraphQLContext
import sangria.schema.{Field, IntType, ObjectType, StringType, fields}

import scala.concurrent.Future

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

object UserProficiency {
  val queryType: ObjectType[GraphQLContext, UserProficiency] = ObjectType(
    "UserProficiency",
    fields[GraphQLContext, UserProficiency](
      Field("username", StringType, resolve = _.value.username),
      Field("topic", Topic.queryType, resolve = _.value.topic),
      Field("points", IntType, resolve = _.value.getPoints),
      Field("pointsForNextLevel", IntType, resolve = _.value.pointsForNextLevel),
      Field("level", Level.queryType, resolve = _.value.getLevel)
    )
  )
}

trait ProficiencyRepository {
  self: TableDefs =>

  import profile.api._

  // FIXME: make view?
  def userProficienciesForToolWithParts(username: String, toolId: String): Future[Seq[UserProficiency]] = {

    val query = userSolutionWithPartsTQ
      // Filter solutions for tool and user, only take completely correct solutions
      .filter { userSol => userSol.toolId === toolId && userSol.username === username && userSol.pointsQuarters === userSol.maxPointsQuarters }
      // filter out multiple solutions
      .distinctOn { userSol => (userSol.toolId, userSol.collectionId, userSol.exerciseId, userSol.partId) }
      // Join with topics for exercise
      .join(exerciseTopicsTQ)
      .on { case (userSol, exTopic) =>
        userSol.toolId === exTopic.toolId && userSol.collectionId === exTopic.collectionId && userSol.exerciseId === exTopic.exerciseId
      }
      .map { case (userSol, exTopic) => (exTopic, userSol.maxPointsQuarters) }
      .result

    val x = for {
      queryResult: Seq[(ExerciseTopicRow, Int)] <- db.run(query)

      result = queryResult
        .groupBy(_._1)
        .map { case (partId, levelsAndMaxPoints) =>
          val points = levelsAndMaxPoints.map { case (topic, maxPointsQuarters) => topic._5.level * (maxPointsQuarters / 4) }.sum

          (partId, points)
        }
    } yield result

    ???
    // db.run(proficienciesTQ.filter { p => p.username === username && p.toolId === toolId }.result)
  }

}
