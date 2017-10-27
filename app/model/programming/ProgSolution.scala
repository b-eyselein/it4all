package model.programming

import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

case class ProgSolution(userId: Int, exerciseId: Int, solution: String)

private[model] trait ProgSolutions extends ProgExercises {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class ProgSolutionTable(tag: Tag) extends Table[ProgSolution](tag, "PROG_SOLUTIONS") {

    def id = column[Int]("ID", O.AutoInc)

    def exerciseId = column[Int]("EXERCISE_ID")

    def solution = column[String]("SOLUTION")

    def pk = primaryKey("PK", (id, exerciseId))

    def exerciseFk = foreignKey("EXERCISE_FK", exerciseId, progExercises)(_.id)


    def * = (id, exerciseId, solution) <> (ProgSolution.tupled, ProgSolution.unapply)

  }

  lazy val progSolutions = TableQuery[ProgSolutionTable]

}