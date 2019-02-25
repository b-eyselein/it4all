package model


trait ExerciseCollection[CompExType <: CollectionExercise] extends HasBaseValues


trait CompleteCollection {

  type CompEx <: CollectionExercise

  type Coll <: ExerciseCollection[CompEx]

  def coll: Coll

  def exercisesWithFilter(filter: String): Seq[CompEx] = exercises

  def exercises: Seq[CompEx]

  def renderRest: play.twirl.api.Html

}
