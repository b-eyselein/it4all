package model

import enumeratum.{EnumEntry, PlayEnum}

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

  val sampleSolutions: Seq[S]

  def parts: Seq[ExPart]

}

trait FileExerciseContent extends ExerciseContent {

  override protected type S = FilesSolution

}

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

final case class UserSolution[SolType, PartType <: ExPart](
  solutionId: Int,
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  username: String,
  solution: SolType,
  part: PartType
)
