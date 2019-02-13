package model


trait ExerciseCollection[CompExType <: CompleteExInColl] extends HasBaseValues


trait CompleteCollection {

  type CompEx <: CompleteExInColl

  type Coll <: ExerciseCollection[CompEx]

  def coll: Coll

  def exercisesWithFilter(filter: String): Seq[CompEx] = exercises

  def exercises: Seq[CompEx]

  def renderRest: play.twirl.api.Html

}
