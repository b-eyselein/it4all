package model.persistence

import model.Enums.ExerciseState
import model.core.ExPart
import model.toolMains.ToolList.STEP
import model.{CompleteEx, Exercise, PartSolution, Solution}
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait ExerciseWithSolTableDefs[Ex <: Exercise, CompEx <: CompleteEx[Ex], SolType <: Solution] extends ExerciseTableDefs[Ex, CompEx] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Abstract types and members

  protected type SolTableDef <: SolutionsTable[SolType]

  protected val solTable: TableQuery[SolTableDef]

  // Queries

  def futureOldSolution(username: String, exerciseId: Int): Future[Option[SolType]] =
    db.run(solTable.filter(sol => sol.username === username && sol.exerciseId === exerciseId).result.headOption)

  def futureSaveSolution(sol: SolType)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(solTable insertOrUpdate sol) map (_ => true) recover {
      case e: Exception =>
        Logger.error("Could not save solution", e)
        false
    }

  // Abstract table definitions

  abstract class SolutionsTable[S <: Solution](tag: Tag, name: String) extends Table[S](tag, name) {

    def username = column[String]("username")

    def exerciseId = column[Int]("exercise_id")


    def pk = primaryKey("pk", (username, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)

    def userFk = foreignKey("user_fk", username, users)(_.username)

  }


}

trait SingleExerciseTableDefs[Ex <: Exercise, CompEx <: CompleteEx[Ex], SolType <: PartSolution, PartType <: ExPart] extends ExerciseWithSolTableDefs[Ex, CompEx, SolType] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  override protected type SolTableDef <: PartSolutionsTable[SolType]

  // Implicit column types

  protected implicit val partTypeColumnType: BaseColumnType[PartType]

  // Queries

  def futureOldSolution(username: String, exerciseId: Int, part: PartType): Future[Option[SolType]] =
    db.run(solTable.filter(sol => sol.username === username && sol.exerciseId === exerciseId && sol.part === part).result.headOption)

  // Abstract table definitions

  abstract class PartSolutionsTable[S <: PartSolution](tag: Tag, name: String) extends SolutionsTable[S](tag, name) {

    def part = column[PartType]("part")

  }

}

trait ExerciseTableDefs[Ex <: Exercise, CompEx <: CompleteEx[Ex]] extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  protected type ExTableDef <: HasBaseValuesTable[Ex]

  protected val exTable: TableQuery[ExTableDef]

  // Numbers

  def futureNumOfExes: Future[Int] = db.run(exTable.length.result)

  // Reading

  def futureCompleteExes(implicit ec: ExecutionContext): Future[Seq[CompEx]] = db.run(exTable.result) flatMap (exes => Future.sequence(exes map completeExForEx))

  def futureCompleteExesForPage(page: Int)(implicit ec: ExecutionContext): Future[Seq[CompEx]] =
    db.run(exTable.filter(_.state === ExerciseState.APPROVED).result) flatMap { allExes =>
      val (sliceStart, sliceEnd) = (Math.max(0, (page - 1) * STEP), Math.min(page * STEP, allExes.size))
      Future.sequence(allExes slice(sliceStart, sliceEnd) map completeExForEx)
    }

  def futureCompleteExById(id: Int)(implicit ec: ExecutionContext): Future[Option[CompEx]] = db.run(exTable.filter(_.id === id).result.headOption) flatMap {
    case Some(ex) => completeExForEx(ex) map Some.apply
    case None     => Future(None)
  }

  def futureHighestId(implicit ec: ExecutionContext): Future[Int] = db.run(exTable.map(_.id).max.result) map (_ getOrElse (-1))

  protected def completeExForEx(ex: Ex)(implicit ec: ExecutionContext): Future[CompEx]

  // Saving

  def saveCompleteEx(compEx: CompEx)(implicit ec: ExecutionContext): Future[Boolean] = db.run(exTable.filter(_.id === compEx.ex.id).delete) flatMap { _ =>
    db.run(exTable += compEx.ex) flatMap { _ => saveExerciseRest(compEx) } recover { case _: Exception => false }
  }

  protected def saveExerciseRest(compEx: CompEx)(implicit ec: ExecutionContext): Future[Boolean]

  // Update

  def updateExerciseState(id: Int, newState: ExerciseState)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run((for {ex <- exTable if ex.id === id} yield ex.state).update(newState)) map (_ => true) recover {
      case e: Throwable =>
        Logger.error(s"Could not update state of exercise $id", e)
        false
    }

  // Deletion

  def deleteExercise(id: Int): Future[Int] = db.run(exTable.filter(_.id === id).delete)


}