package model.web

import model._
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

case class WebSolution(exerciseId: Int, userName: String, solution: String)

private[model] trait WebSolutions extends TableDefs with WebExercises {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class WebSolutionsTable(tag: Tag) extends Table[WebSolution](tag, "WEB_SOLUTIONS") {

    def exerciseId: Rep[Int] = column[Int]("EXERCISE_ID")

    def userName = column[String]("USER_NAME")

    def solution = column[String]("SOLUTION")

    def pk = primaryKey("primaryKey", (exerciseId, userName))

    def exerciseFk = foreignKey("EXERCISE_FK", exerciseId, webExercises)(_.id)

    def userFk = foreignKey("USER_FK", userName, users)(_.username)

    def * = (exerciseId, userName, solution) <> (WebSolution.tupled, WebSolution.unapply)

  }

  lazy val webSolutions = TableQuery[WebSolutionsTable]

}
