package model.core

import model.Enums.ExerciseState

class Exercise(i: Int, ti: String, a: String, te: String, s: ExerciseState) extends HasBaseValues(i, ti, a, te, s) {

  override def tags: List[ExTag] = List.empty

}
