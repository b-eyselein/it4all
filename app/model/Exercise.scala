package model

import model.Enums.ExerciseState
import play.twirl.api.Html

case class BaseValues(id: Int, title: String, author: String, text: String, state: ExerciseState)

abstract class HasBaseValues(val baseValues: BaseValues) {

  def this(id: Int, title: String, author: String, text: String, state: ExerciseState) = this(BaseValues(id, title, author, text, state))

  def id: Int = baseValues.id

  def title: String = baseValues.title

  def author: String = baseValues.author

  def text: String = baseValues.text

  def state: ExerciseState = baseValues.state

  def renderRest: Html = ???

}

trait ExTag {

  def render = new Html(s"""<span class="$cssClass" title="$title">$buttonContent</span>""")

  def cssClass = "label label-primary"

  def buttonContent: String = toString

  def title: String = toString

}

trait CompleteEx[B <: HasBaseValues] {

  def ex: HasBaseValues

  def renderRest: Html = new Html("")

  def tags: List[ExTag] = List.empty

}

abstract class Exercise(bvs: BaseValues) extends HasBaseValues(bvs) {

  def this(i: Int, ti: String, a: String, te: String, s: ExerciseState) = this(BaseValues(i, ti, a, te, s))

  def renderEditRest(isCreation: Boolean) = new Html("")

}