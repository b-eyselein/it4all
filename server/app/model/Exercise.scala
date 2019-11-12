package model

import play.twirl.api.Html


trait ExPart {

  // FIXME: use enumeratum =?=

  def urlName: String

  def partName: String

}

trait ExTag {

  val buttonContent: String

  val title: String

}

trait Exercise {

  val id: Int

  val collId: Int

  val semanticVersion: SemanticVersion

  val title: String

  val author: String

  val text: String

  val state: ExerciseState


  def preview: Html

  def tags: Seq[ExTag] = Seq[ExTag]()

}

trait FileExercise[PartType <: ExPart] {
  self: Exercise =>

  def filesForExercisePart(part: PartType): LoadExerciseFilesMessage

}

final case class ExerciseCollection(
  id: Int,
  toolId: String,
  title: String,
  author: String,
  text: String,
  state: ExerciseState,
  shortName: String
)
