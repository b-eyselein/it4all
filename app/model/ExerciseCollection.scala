package model


trait ExerciseCollection extends HasBaseValues {

  val shortName: String

}


trait CompleteCollection {

  type CompEx <: Exercise

  type Coll <: ExerciseCollection

  def coll: Coll

  def exercises: Seq[CompEx]

  def renderRest: play.twirl.api.Html

}
