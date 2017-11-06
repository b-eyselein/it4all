package model.core

import com.fasterxml.jackson.annotation.{JsonGetter, JsonIgnore}
import model.Enums.ExerciseState
import model.Exercise
import model.core.HasBaseValues.SPLITTER
import play.twirl.api.Html

abstract class ExerciseCollection[E <: Exercise](i: Int, ti: String, a: String, te: String, s: ExerciseState = ExerciseState.RESERVED)
  extends HasBaseValues(i, ti, a, te, s) {

  def exercises: List[E]

  @JsonIgnore
  val maxPoints = 0

  def restHeaders: List[String] = List.empty

  @JsonGetter("text")
  def textForJson: java.util.List[String] = SPLITTER.splitToList(text)

  override def renderRest: Html = new Html("")

}
