package model

import scala.concurrent.Future

trait ExerciseTopicsRepository {
  self: TableDefs =>

  import profile.api._

  protected type ExerciseTopicRow = (String, Int, Int, String, Level)

  protected val exerciseTopicsTQ = TableQuery[ExerciseTopicsTable]

  def futureInsertTopicsForExercise(toolId: String, collectionId: Int, exerciseId: Int, topics: Map[Topic, Level]): Future[Int] = for {
    maybeRowCount <- db.run(exerciseTopicsTQ ++= topics.map { case (topic, level) => (toolId, collectionId, exerciseId, topic.abbreviation, level) })
  } yield maybeRowCount.getOrElse(0)

  def futureTopicsForExercise(toolId: String, collectionId: Int, exerciseId: Int): Future[Seq[TopicWithLevel]] = for {
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

  protected class ExerciseTopicsTable(tag: Tag) extends Table[ExerciseTopicRow](tag, "exercise_topics") {

    def toolId            = column[String]("tool_id")
    def collectionId      = column[Int]("collection_id")
    def exerciseId        = column[Int]("exercise_id")
    def topicAbbreviation = column[String]("topic_abbreviation")
    def level             = column[Level]("level")

    def pk = primaryKey("exercise_topics_pk", (toolId, collectionId, exerciseId, topicAbbreviation))

    def exerciseForeignKey = foreignKey("exercise_topics_exercise_fk", (toolId, collectionId, exerciseId), exercisesTQ)(
      ex => (ex.toolId, ex.collectionId, ex.exerciseId),
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade
    )
    def topicsForeignKey = foreignKey("exercise_topics_topic_fk", (toolId, topicAbbreviation), topicsTQ)(
      t => (t.toolId, t.abbreviation),
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade
    )

    override def * = (toolId, collectionId, exerciseId, topicAbbreviation, level)
  }

}
