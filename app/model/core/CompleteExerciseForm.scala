package model.core

import model.{CompleteEx, Exercise}
import play.api.data.Form

trait CompleteExerciseForm[CompExType <: CompleteEx[_ <: Exercise]] {

  type FormData

  val format: Form[CompExType]

  protected def unapplyCompEx(compEx: CompExType): Option[FormData]

}
