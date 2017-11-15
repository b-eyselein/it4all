package model.core

import model.Enums.ExerciseState
import model.{Exercise, HasBaseValues}
import play.twirl.api.Html

abstract class ExerciseCollection[E <: Exercise](i: Int, ti: String, a: String, te: String, s: ExerciseState = ExerciseState.RESERVED)
  extends HasBaseValues(i, ti, a, te, s) {

  def exercises: List[E]

  val maxPoints = 0

  def restHeaders: List[String] = List.empty


  override def renderRest: Html = new Html("")

}
