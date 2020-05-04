package model.tools

final case class Topic(
  abbreviation: String,
  toolId: String,
  title: String,
  maxLevel: Int = 3
)

final case class ExerciseCollection(
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String
)

trait ExerciseContent[S] {

  val sampleSolutions: Seq[SampleSolution[S]]

  def parts: Seq[ExPart]

}

final case class TopicWithLevel(
  topic: Topic,
  level: Int
)

final case class Exercise[S, C <: ExerciseContent[S]](
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  topicAbbreviations: Seq[(String, Int)],
  difficulty: Int,
  content: C
)

final case class ExerciseFile(
  name: String,
  fileType: String,
  editable: Boolean,
  content: String
)

final case class SampleSolution[SolType](
  id: Int,
  sample: SolType
)

final case class UserSolution[SolType](
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  username: String,
  solution: SolType
)
