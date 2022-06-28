package model

import enumeratum.EnumEntry

trait ExPart extends EnumEntry {

  val id: String

  val partName: String

  def isEntryPart: Boolean = true

}

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
