package model

import model.graphql.GraphQLContext
import sangria.schema.{Field, ObjectType, StringType, fields}

import scala.concurrent.Future

final case class Topic(
  abbreviation: String,
  toolId: String,
  title: String
)

object Topic {
  val queryType: ObjectType[GraphQLContext, Topic] = ObjectType(
    "Topic",
    fields[GraphQLContext, Topic](
      Field("toolId", StringType, resolve = _.value.toolId),
      Field("abbreviation", StringType, resolve = _.value.abbreviation),
      Field("title", StringType, resolve = _.value.title)
    )
  )
}

final case class TopicWithLevel(
  topic: Topic,
  level: Level
)

object TopicWithLevel {
  val queryType: ObjectType[GraphQLContext, TopicWithLevel] = ObjectType(
    "TopicWithLevel",
    fields[GraphQLContext, TopicWithLevel](
      Field("topic", Topic.queryType, resolve = _.value.topic),
      Field("level", Level.queryType, resolve = _.value.level)
    )
  )
}

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

    override def * = (abbreviation, toolId, title).mapTo[Topic]
  }
}
