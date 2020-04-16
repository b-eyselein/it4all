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

trait Exercise {
  type SolType

  val id: Int
  val collectionId: Int
  val toolId: String

  val title: String
  val authors: Seq[String]
  val text: String
  val topics: Seq[Topic]
  val difficulty: Option[Int]

  val sampleSolutions: Seq[SampleSolution[SolType]]
}

final case class SampleSolution[SolType](
  id: Int,
  sample: SolType
)

trait ExerciseContent {

  type SolType

  val sampleSolutions: Seq[SampleSolution[SolType]]

}

final case class ExerciseFile(
  name: String,
  fileType: String,
  editable: Boolean,
  content: String
)

trait StringExerciseContent extends ExerciseContent {

  override type SolType = String

}
