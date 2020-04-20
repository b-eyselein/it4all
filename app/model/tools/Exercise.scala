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

trait ExerciseContent[S] {
  val sampleSolutions: Seq[SampleSolution[S]]
}

final case class Exercise[S, C <: ExerciseContent[S]](
  id: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  topics: Seq[Topic],
  difficulty: Int,
  content: C
)

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
