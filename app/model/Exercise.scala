package model

import enumeratum.{EnumEntry, PlayEnum}

final case class Topic(
  abbreviation: String,
  toolId: String,
  title: String,
  maxLevel: Level = Level.Expert
)

final case class ExerciseCollection(
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String]
)

trait ExPart extends EnumEntry {

  val id: String

  val partName: String

  def isEntryPart: Boolean = true

}

trait ExParts[EP <: ExPart] extends PlayEnum[EP]

trait ExerciseContent {

  protected type S

  val sampleSolutions: Seq[SampleSolution[S]]

  def parts: Seq[ExPart]

}

final case class TopicWithLevel(
  topic: Topic,
  level: Level
)

final case class Exercise[C <: ExerciseContent](
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  topicsWithLevels: Seq[TopicWithLevel] = Seq.empty,
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

object UserSolution {

  def forExercise[S, P <: ExPart](
    solutionId: Int,
    exercise: Exercise[_ <: ExerciseContent],
    username: String,
    solution: S,
    part: P
  ): UserSolution[S, P] =
    UserSolution(solutionId, exercise.exerciseId, exercise.collectionId, exercise.toolId, username, solution, part)

}

final case class UserSolution[SolType, PartType <: ExPart](
  solutionId: Int,
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  username: String,
  solution: SolType,
  part: PartType
)
