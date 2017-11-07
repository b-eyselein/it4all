package model.core

import play.twirl.api.Html

abstract class CompleteEx[B <: HasBaseValues] {

  def ex: HasBaseValues

  def renderRest: Html = new Html("")

  def tags: List[ExTag] = List.empty

}
