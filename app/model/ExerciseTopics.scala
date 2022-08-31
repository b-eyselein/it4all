package model

import model.tools.ToolList

import scala.concurrent.Future

//noinspection ScalaFileName
trait ExerciseTopicsRepository {
  self: TableDefs =>

  import MyPostgresProfile.api._

  private type Row = (String, Int, Int, String, Level)

  private val exerciseTopicsTQ = TableQuery[ExerciseTopicsTable]

  /*
  def futureInsertTopicForExercise(toolId: String, collectionId: Int, exerciseId: Int, topicAbbreviation: String, level: Level): Future[Int] =
    db.run(exerciseTopicsTQ += (toolId, collectionId, exerciseId, topicAbbreviation, level))
   */

  def futureInsertTopicsForExercise(toolId: String, collectionId: Int, exerciseId: Int, topics: Map[Topic, Level]): Future[Int] = for {
    maybeRowCount <- db.run(exerciseTopicsTQ ++= topics.map { case (topic, level) => (toolId, collectionId, exerciseId, topic.abbreviation, level) })
  } yield maybeRowCount.getOrElse(0)

  def futureTopicsForExercise(toolId: String, collectionId: Int, exerciseId: Int): Future[Seq[TopicWithLevel]] = for {
    tool <- ToolList.tools.find(_.id == toolId) match {
      case Some(tool) => Future.successful(tool)
      case None       => Future.failed(new Exception(s"No such tool with id $toolId"))
    }

    topicLevelTuples <- db.run(
      exerciseTopicsTQ
        .filter { row => row.toolId === toolId && row.collectionId === collectionId && row.exerciseId === exerciseId }
        .join(topicsTQ)
        .on { case (exerciseTopic, topic) =>
          exerciseTopic.toolId === topic.toolId && exerciseTopic.topicAbbreviation === topic.abbreviation
        }
        .map { case (exerciseTopic, topic) => (topic, exerciseTopic.level) }
        .result
    )

    topicWithLevels = topicLevelTuples.map { case (topic, level) => TopicWithLevel(topic, level) }

  } yield topicWithLevels

  protected class ExerciseTopicsTable(tag: Tag) extends Table[Row](tag, "exercise_topics") {

    def toolId = column[String]("tool_id")

    def collectionId = column[Int]("collection_id")

    def exerciseId = column[Int]("exercise_id")

    def topicAbbreviation = column[String]("topic_abbreviation")

    def level = column[Level]("level")

    def topicsForeignKey = foreignKey("exercise_topics_topic_fk", (toolId, topicAbbreviation), topicsTQ)(
      t => (t.toolId, t.abbreviation),
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade
    )

    override def * = (toolId, collectionId, exerciseId, topicAbbreviation, level)

  }

}
