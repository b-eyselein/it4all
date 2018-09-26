package model.core

import model.{CompleteEx, Exercise}
import play.api.data.Form

trait CompleteExerciseForm[ExType <: Exercise, CompExType <: CompleteEx[ExType]] {

  type FormData

  val format: Form[CompExType]

  def unapplyCompEx(compEx: CompExType): Option[FormData]

}
