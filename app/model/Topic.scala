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

  def futureTopicsForTool(toolId: String): Future[Seq[Topic]] = db.run(topicsTQ.filter(_.toolId === toolId).result)

  def futureTopicByAbbreviation(toolId: String, abbreviation: String): Future[Option[Topic]] = db.run(
    topicsTQ.filter { t => t.toolId === toolId && t.abbreviation === abbreviation }.result.headOption
  )

  protected class TopicsTable(tag: Tag) extends Table[Topic](tag, "topics") {

    def toolId = column[String]("tool_id")

    def abbreviation = column[String]("abbreviation")

    def title = column[String]("abbreviation")

    override def * = (abbreviation, toolId, title) <> (Topic.tupled, Topic.unapply)

  }
}
