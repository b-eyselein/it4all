package model


trait ExerciseCollection[ExType <: ExInColl, CompExType <: CompleteExInColl[ExType]] extends HasBaseValues


trait CompleteCollection  {

  type Ex <: ExInColl

  type CompEx <: CompleteExInColl[Ex]

  type Coll <: ExerciseCollection[Ex, CompEx]

  def coll: Coll

  def exercisesWithFilter(filter: String): Seq[CompEx] = exercises

  def exercises: Seq[CompEx]

  def renderRest: play.twirl.api.Html

}
