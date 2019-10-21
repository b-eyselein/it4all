package model

import play.twirl.api.Html


trait ExPart {

  // FIXME: use enumeratum =?=

  def urlName: String

  def partName: String

}

trait ExTag {

  def render: Html = new Html(s"""<span class="$cssClass" title="$title">$buttonContent</span>""")

  def cssClass: String = "badge badge-primary"

  def buttonContent: String

  def title: String

}

trait Exercise {

  def id: Int

  // FIXME: remove? semantic version!
  def semanticVersion: SemanticVersion

  def title: String

  def author: String

  def text: String

  def state: ExerciseState


  def preview: Html

  def tags: Seq[ExTag] = Seq[ExTag]()

}

trait FileExercise[PartType <: ExPart] {
  self: Exercise =>

  def filesForExercisePart(part: PartType): LoadExerciseFilesMessage

}

final case class ExerciseCollection(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
