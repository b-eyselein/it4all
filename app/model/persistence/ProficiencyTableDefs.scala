package model.persistence

import model.User
import model.adaption.{Proficiencies, ToolProficiency, Topic, TopicProficiency}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.Future

trait ProficiencyTableDefs extends HasDatabaseConfigProvider[JdbcProfile] {
  self: TableDefs =>

  import profile.api._

  protected val toolProficienciesTQ: TableQuery[ToolProficienciesTable]   = TableQuery[ToolProficienciesTable]
  protected val topicProficienciesTQ: TableQuery[TopicProficienciesTable] = TableQuery[TopicProficienciesTable]

  private def toolProficiency(username: String, toolId: String): Future[Option[ToolProficiency]] =
    db.run(
      toolProficienciesTQ
        .filter { tp =>
          tp.username === username && tp.toolId === toolId
        }
        .result
        .headOption
    )

  private def topicProficiencies(username: String, toolId: String): Future[Seq[TopicProficiency]] =
    db.run(
      topicProficienciesTQ.filter { tp =>
        tp.username === username && tp.toolId === toolId
      }.result
    )

  def futureProficiencies(username: String, toolId: String): Future[Option[Proficiencies]] =
    for {
      toolProficiency    <- toolProficiency(username, toolId)
      topicProficiencies <- topicProficiencies(username, toolId)
    } yield toolProficiency.map(Proficiencies(_, topicProficiencies))

  protected class TopicsTable(tag: Tag) extends Table[Topic](tag, "topics") {

    def id = column[Int]("id")

    def toolId = column[String]("tool_id")

    def name = column[String]("name")

    def pk = primaryKey("pk", (id, toolId))

    override def * : ProvenShape[Topic] = (id, toolId, name) <> (Topic.tupled, Topic.unapply)

  }

  protected class ToolProficienciesTable(tag: Tag) extends Table[ToolProficiency](tag, "tool_proficiencies") {

    def username: Rep[String] = column[String]("username")

    def toolId: Rep[String] = column[String]("tool_id")

    def points: Rep[Int] = column[Int]("points")

    def pk: PrimaryKey = primaryKey("pk", (username, toolId))

    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)

    override def * : ProvenShape[ToolProficiency] =
      (username, toolId, points) <> (ToolProficiency.tupled, ToolProficiency.unapply)

  }

  protected class TopicProficienciesTable(tag: Tag) extends Table[TopicProficiency](tag, "topic_proficiencies") {

    def username: Rep[String] = column[String]("username")

    def toolId: Rep[String] = column[String]("tool_id")

    def topicId: Rep[Int] = column[Int]("topic_id")

    def points: Rep[Int] = column[Int]("points")

    def pk: PrimaryKey = primaryKey("pk", (username, toolId))

    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)

    override def * : ProvenShape[TopicProficiency] =
      (username, toolId, topicId, points) <> (TopicProficiency.tupled, TopicProficiency.unapply)

  }

}
