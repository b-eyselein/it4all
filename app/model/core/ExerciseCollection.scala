package model.core

import model.{BaseValues, Exercise, HasBaseValues}

abstract class ExerciseCollection[E <: Exercise](val baseValues: BaseValues)  extends HasBaseValues {

  def exercises: List[E]

  val maxPoints = 0

  //  def restHeaders: List[String] = List.empty

  //  override def renderRest: Html = new Html("")

}
