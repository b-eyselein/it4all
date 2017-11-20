package model

import model.Enums.ExerciseState
import play.twirl.api.Html

case class BaseValues(id: Int, title: String, author: String, text: String, state: ExerciseState)

trait HasBaseValues {

  val baseValues: BaseValues

  def id: Int = baseValues.id

  def title: String = baseValues.title

  def author: String = baseValues.author

  def text: String = baseValues.text

  def state: ExerciseState = baseValues.state

}

trait ExTag {

  def render = new Html(s"""<span class="$cssClass" title="$title">$buttonContent</span>""")

  def cssClass = "label label-primary"

  def buttonContent: String = toString

  def title: String = toString

}

trait CompleteEx[B <: HasBaseValues] {

  def ex: HasBaseValues

  def preview: Html = ???

  def tags: List[ExTag] = List.empty

  def renderListRest: Html = ???

}

abstract class Exercise(val baseValues: BaseValues) extends HasBaseValues {

  def this(i: Int, ti: String, a: String, te: String, s: ExerciseState) = this(BaseValues(i, ti, a, te, s))

  def renderEditRest(isCreation: Boolean) = new Html("")

}