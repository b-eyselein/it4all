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

final case class Exercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  topics: Seq[Topic],
  difficulty: Int
)

final case class SampleSolution[SolType](
  id: Int,
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  sample: SolType
)

final case class ExerciseFile(
  name: String,
  fileType: String,
  editable: Boolean,
  content: String
)
