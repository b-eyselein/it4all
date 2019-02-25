package model


trait ExerciseCollection[CompExType <: Exercise] extends HasBaseValues


trait CompleteCollection {

  type CompEx <: Exercise

  type Coll <: ExerciseCollection[CompEx]

  def coll: Coll

  def exercisesWithFilter(filter: String): Seq[CompEx] = exercises

  def exercises: Seq[CompEx]

  def renderRest: play.twirl.api.Html

}
