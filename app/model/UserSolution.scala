package model

import model.points.Points
import play.api.libs.json.{Format, JsValue}

import scala.concurrent.{ExecutionContext, Future}

private final case class DbUserSolution(
  toolId: String,
  collectionId: Int,
  exerciseId: Int,
  username: String,
  partId: String,
  solutionId: Int,
  jsonSolution: JsValue,
  pointsQuarters: Int,
  maxPointsQuarters: Int
)

trait UserSolutionRepository {
  self: TableDefs =>

  import MyPostgresProfile.api._

  protected implicit val ec: ExecutionContext

  private val userSolutionTQ = TableQuery[UserSolutionsTable]

  private def solutionForUserAndExercisePart(toolId: String, collectionId: Int, exerciseId: Int, username: String, partId: String) = userSolutionTQ
    .filter { us =>
      us.toolId === toolId && us.collectionId === collectionId && us.exerciseId === exerciseId && us.username === username && us.partId === partId
    }

  def futureNextUserSolutionId(toolId: String, collectionId: Int, exerciseId: Int, username: String, partId: String): Future[Int] = for {
    maybeMaxSolutionId <- db.run(
      solutionForUserAndExercisePart(toolId, collectionId, exerciseId, username, partId)
        .map(_.solutionId)
        .max
        .result
    )
  } yield maybeMaxSolutionId.map(_ + 1).getOrElse(0)

  def futureInsertSolution[S](
    toolId: String,
    collectionId: Int,
    exerciseId: Int,
    username: String,
    solution: S,
    part: ExPart,
    solutionFormat: Format[S],
    points: Points,
    maxPoints: Points
  ): Future[Int] = for {
    nextUserSolutionId <- futureNextUserSolutionId(toolId, collectionId, exerciseId, username, part.id)

    _ <- db.run(
      userSolutionTQ += DbUserSolution(
        toolId,
        collectionId,
        exerciseId,
        username,
        part.id,
        nextUserSolutionId,
        solutionFormat.writes(solution),
        points.quarters,
        maxPoints.quarters
      )
    )
  } yield nextUserSolutionId

  def futureUserHasCorrectExerciseResult(toolId: String, collectionId: Int, exerciseId: Int, username: String, partId: String): Future[Boolean] = for {
    bestTryCompletelyCorrect <- db.run(
      solutionForUserAndExercisePart(toolId, collectionId, exerciseId, username, partId)
        .sortBy(_.pointsQuarters.desc)
        .map { us => us.pointsQuarters === us.maxPointsQuarters }
        .result
        .headOption
    )
  } yield bestTryCompletelyCorrect.getOrElse(false)

  private class UserSolutionsTable(tag: Tag) extends Table[DbUserSolution](tag, "user_solutions") {

    // Primary key cols

    def toolId = column[String]("tool_id")

    def collectionId = column[Int]("collection_id")

    def exerciseId = column[Int]("exercise_id")

    def username = column[String]("username")

    def solutionId = column[Int]("solution_id")

    def partId = column[String]("part_id")

    // Other cols

    def jsonSolution = column[JsValue]("solution_json")

    def pointsQuarters = column[Int]("points_quarters")

    def maxPointsQuarters = column[Int]("max_points_quarters")

    // Key defs

    def pk = primaryKey("user_solutions_pk", (toolId, collectionId, exerciseId, username, partId, solutionId, partId))

    def exerciseForeignKey = foreignKey("user_solutions_exercise_fk", (toolId, collectionId, exerciseId), exercisesTQ)(
      ex => (ex.toolId, ex.collectionId, ex.exerciseId),
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade
    )

    def userForeignKey =
      foreignKey("user_solutions_user_fk", username, usersTQ)(_.username, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

    override def * = (
      toolId,
      collectionId,
      exerciseId,
      username,
      partId,
      solutionId,
      jsonSolution,
      pointsQuarters,
      maxPointsQuarters
    ) <> (DbUserSolution.tupled, DbUserSolution.unapply)

  }

}
