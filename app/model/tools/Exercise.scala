package model.tools

final case class Topic(
  id: Int,
  toolId: String,
  abbreviation: String,
  title: String
)

final case class ExerciseCollection(
  id: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  shortName: String
)

trait Exercise[SolType, ContentType] {
  val id: Int
  val collectionId: Int
  val toolId: String

  val title: String
  val authors: Seq[String]
  val text: String
  val topics: Seq[Topic]
  val difficulty: Int

  val sampleSolutions: Seq[SampleSolution[SolType]]
  val content: ContentType
}

final case class SampleSolution[SolType](
  id: Int,
  sample: SolType
)

final case class ExerciseFile(
  name: String,
  fileType: String,
  editable: Boolean,
  content: String
)
