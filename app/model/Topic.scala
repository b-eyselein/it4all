package model

import scala.concurrent.Future

final case class Topic(
  abbreviation: String,
  toolId: String,
  title: String
)

final case class TopicWithLevel(
  topic: Topic,
  level: Level
)

trait TopicRepository {
  self: TableDefs =>

  import profile.api._

  protected val topicsTQ = TableQuery[TopicsTable]

  def futureInsertTopics(topics: Seq[Topic]): Future[Unit] = for {
    _ <- db.run(topicsTQ ++= topics)
  } yield ()

  def futureTopicsForTool(toolId: String): Future[Seq[Topic]] = db.run(topicsTQ.filter { _.toolId === toolId }.result)

  protected class TopicsTable(tag: Tag) extends Table[Topic](tag, "topics") {
    def toolId       = column[String]("tool_id")
    def abbreviation = column[String]("abbreviation")
    def title        = column[String]("title")

    def pk = primaryKey("topics_pk", (toolId, abbreviation))

    override def * = (abbreviation, toolId, title) <> (Topic.tupled, Topic.unapply)
  }
}
