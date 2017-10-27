package model.web

import com.fasterxml.jackson.annotation.JsonIgnore
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

case class Condition(id: Int, taskId: Int, exerciseId: Int, xpathQuery: String, isPrecondition: Boolean, awaitedValue: String) {

  @JsonIgnore
  def description =
    s"""Element mit XPath "$xpathQuery" sollte den Inhalt "$awaitedValue" haben"""

}

private[model] trait Conditions extends WebTasks {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class ConditionsTable(tag: Tag) extends Table[Condition](tag, "CONDITIONS") {

    def conditionId = column[Int]("CONDITION_ID")

    def taskId = column[Int]("TASK_ID")

    def exerciseId = column[Int]("EXERCISE_ID")

    def xpathQuery = column[String]("XPATH_QUERY")

    def isPrecondition = column[Boolean]("IS_PRECONDITION")

    def awaitedValue = column[String]("AWAITED_VALUE")


    def pk = primaryKey("PK", (conditionId, taskId, exerciseId))

    def taskFk = foreignKey("TASK_FK", (taskId, exerciseId), jsTasks)(t => (t.id, t.exerciseId))


    def * = (conditionId, taskId, exerciseId, xpathQuery, isPrecondition, awaitedValue) <> (Condition.tupled, Condition.unapply)

  }

  lazy val conditions = TableQuery[ConditionsTable]

}
