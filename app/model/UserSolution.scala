package model

import model.points.Points
import play.api.libs.json.{Format, JsValue}

import scala.concurrent.{ExecutionContext, Future}

protected sealed trait DbUserSolution

protected final case class DbUserSolutionWithoutPart(
  toolId: String,
  collectionId: Int,
  exerciseId: Int,
  username: String,
  solutionId: Int,
  jsonSolution: JsValue,
  pointsQuarters: Int,
  maxPointsQuarters: Int
) extends DbUserSolution

protected final case class DbUserSolutionWithPart(
  toolId: String,
  collectionId: Int,
  exerciseId: Int,
  username: String,
  partId: String,
  solutionId: Int,
  jsonSolution: JsValue,
  pointsQuarters: Int,
  maxPointsQuarters: Int
) extends DbUserSolution

trait UserSolutionRepository {
  self: TableDefs =>

  import MyPostgresProfile.api._

  protected implicit val ec: ExecutionContext

  protected val userSolutionWithPartsTQ    = TableQuery[UserSolutionsWithPartsTable]
  protected val userSolutionWithoutPartsTQ = TableQuery[UserSolutionsWithoutPartsTable]

  private def solForUserExAndPart(toolId: String, collectionId: Int, exerciseId: Int, username: String, partId: String) = userSolutionWithPartsTQ.filter { us =>
    us.toolId === toolId && us.collectionId === collectionId && us.exerciseId === exerciseId && us.username === username && us.partId === partId
  }

  private def solForUserAndEx(toolId: String, collectionId: Int, exerciseId: Int, username: String) = userSolutionWithoutPartsTQ.filter { us =>
    us.toolId === toolId && us.collectionId === collectionId && us.exerciseId === exerciseId && us.username === username
  }

  private def futureNextUserSolutionId(toolId: String, collectionId: Int, exerciseId: Int, username: String, maybePartId: Option[String]): Future[Int] = {
    val query = maybePartId match {
      case Some(partId) => solForUserExAndPart(toolId, collectionId, exerciseId, username, partId)
      case None         => solForUserAndEx(toolId, collectionId, exerciseId, username)
    }

    for {
      maybeMaxSolutionId <- db.run(query.map(_.solutionId).max.result)
    } yield maybeMaxSolutionId.map(_ + 1).getOrElse(0)
  }

  def futureInsertSolutionWithPart[S](
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
    nextUserSolutionId <- futureNextUserSolutionId(toolId, collectionId, exerciseId, username, Some(part.id))

    _ <- db.run(
      userSolutionWithPartsTQ += DbUserSolutionWithPart(
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

  def futureInsertSolutionWithoutPart[S](
    toolId: String,
    collectionId: Int,
    exerciseId: Int,
    username: String,
    solution: S,
    solutionFormat: Format[S],
    points: Points,
    maxPoints: Points
  ): Future[Int] = for {
    nextUserSolutionId <- futureNextUserSolutionId(toolId, collectionId, exerciseId, username, None)

    _ <- db.run(
      userSolutionWithoutPartsTQ += DbUserSolutionWithoutPart(
        toolId,
        collectionId,
        exerciseId,
        username,
        nextUserSolutionId,
        solutionFormat.writes(solution),
        points.quarters,
        maxPoints.quarters
      )
    )
  } yield nextUserSolutionId

  def futureUserHasCorrectExerciseResult(toolId: String, collectionId: Int, exerciseId: Int, username: String, maybePartId: Option[String]): Future[Boolean] = {
    val query = maybePartId match {
      case Some(partId) => solForUserExAndPart(toolId, collectionId, exerciseId, username, partId)
      case None         => solForUserAndEx(toolId, collectionId, exerciseId, username)
    }

    for {
      bestTryCompletelyCorrect <- db.run {
        query.sortBy(_.pointsQuarters.desc).map { us => us.pointsQuarters === us.maxPointsQuarters }.result.headOption
      }
    } yield bestTryCompletelyCorrect.getOrElse(false)
  }

  protected abstract class UserSolutionsTable[DbSolType <: DbUserSolution](tag: Tag, _tableName: String) extends Table[DbSolType](tag, _tableName) {

    // Primary key cols

    def toolId = column[String]("tool_id")

    def collectionId = column[Int]("collection_id")

    def exerciseId = column[Int]("exercise_id")

    def username = column[String]("username")

    def solutionId = column[Int]("solution_id")

    // Other cols

    def jsonSolution = column[JsValue]("solution_json")

    def pointsQuarters = column[Int]("points_quarters")

    def maxPointsQuarters = column[Int]("max_points_quarters")

    // Key defs

    // noinspection ScalaUnusedSymbol
    def exerciseForeignKey = foreignKey("user_solutions_exercise_fk", (toolId, collectionId, exerciseId), exercisesTQ)(
      ex => (ex.toolId, ex.collectionId, ex.exerciseId),
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade
    )

    // noinspection ScalaUnusedSymbol
    def userForeignKey =
      foreignKey("user_solutions_user_fk", username, usersTQ)(_.username, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  }

  protected class UserSolutionsWithPartsTable(tag: Tag) extends UserSolutionsTable[DbUserSolutionWithPart](tag, "user_solutions_with_parts") {

    def partId = column[String]("part_id")

    // noinspection ScalaUnusedSymbol
    def pk = primaryKey("user_solutions_pk", (toolId, collectionId, exerciseId, username, partId, solutionId))

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
    ) <> (DbUserSolutionWithPart.tupled, DbUserSolutionWithPart.unapply)

  }

  protected class UserSolutionsWithoutPartsTable(tag: Tag) extends UserSolutionsTable[DbUserSolutionWithoutPart](tag, "user_solutions_without_parts") {

    // noinspection ScalaUnusedSymbol
    def pk = primaryKey("user_solutions_pk", (toolId, collectionId, exerciseId, username, solutionId))

    override def * = (
      toolId,
      collectionId,
      exerciseId,
      username,
      solutionId,
      jsonSolution,
      pointsQuarters,
      maxPointsQuarters
    ) <> (DbUserSolutionWithoutPart.tupled, DbUserSolutionWithoutPart.unapply)

  }

}
