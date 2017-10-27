package model.web

import org.openqa.selenium.{By, SearchContext}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

case class Action(actionId: Int, taskId: Int, exerciseId: Int, actionType: ActionType, xpathQuery: String, keysToSend: String) {

  def perform(context: SearchContext): Boolean = Option(context.findElement(By.xpath(xpathQuery))) match {
    case None          => false
    case Some(element) => actionType match {
      case ActionType.CLICK   =>
        element.click()
        true
      case ActionType.FILLOUT =>
        element.sendKeys(keysToSend)
        // click on other element to fire the onchange event...
        context.findElement(By.xpath("//body")).click()
        true
      case _                  => false
    }
  }

}

private[model] trait Actions extends WebTasks {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  implicit val ActionTypeColumnType: BaseColumnType[ActionType] =
    MappedColumnType.base[ActionType, String](_.name, str => Option(ActionType.valueOf(str)).getOrElse(ActionType.CLICK))


  class ActionsTable(tag: Tag) extends Table[Action](tag, "ACTIONS") {

    def actionId = column[Int]("CONDITION_ID")

    def taskId = column[Int]("TASK_ID")

    def exerciseId = column[Int](
      "EXERCISE_ID")

    def actionType = column[ActionType]("ACTION_TYPE")

    def xpathQuery = column[String]("XPATH_QUERY")

    def keysToSend = column[String]("KEYS_TO_SEND")


    def pk = primaryKey("PK", (actionId, taskId, exerciseId))

    def taskFk = foreignKey("TASK_FK", (taskId, exerciseId), jsTasks)(t => (t.id, t.exerciseId))


    def * = (actionId, taskId, exerciseId, actionType, xpathQuery, keysToSend) <> (Action.tupled, Action.unapply)

  }

  lazy val conditions = TableQuery[ActionsTable]

}
