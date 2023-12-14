package model

import enumeratum.EnumEntry
import play.api.libs.json.JsValue

trait ExPart extends EnumEntry {
  def id: String
  def partName: String
  def isEntryPart: Boolean = true
}

trait ExerciseContent {
  protected type S

  def sampleSolutions: Seq[S]

  def parts: Seq[ExPart] = Seq.empty
}

trait FileExerciseContent extends ExerciseContent {
  override protected type S = FilesSolution
}

final case class Exercise[C <: ExerciseContent](
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  text: String,
  difficulty: Level,
  content: C
)

protected final case class DbExercise(
  toolId: String,
  collectionId: Int,
  exerciseId: Int,
  title: String,
  text: String,
  difficulty: Level,
  jsonContent: JsValue
)
