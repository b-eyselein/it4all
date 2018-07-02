package model


trait ExerciseCollection[ExType <: Exercise, CompExType <: CompleteEx[ExType]] extends HasBaseValues


trait CompleteCollection  {

  type Ex <: Exercise

  type CompEx <: CompleteEx[Ex]

  type Coll <: ExerciseCollection[Ex, CompEx]

  def coll: Coll

  def exercisesWithFilter(filter: String): Seq[CompEx] = exercises

  def exercises: Seq[CompEx]

  def renderRest: play.twirl.api.Html

//  override def id: Int = coll.id
//
//  override def title: String = coll.title
//
//  override def author: String = coll.author
//
//  override def text: String = coll.text
//
//  override def state: ExerciseState = coll.state

}
