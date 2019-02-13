package model.core

import model.CompleteEx
import play.api.data.Form

trait CompleteExerciseForm[CompExType <: CompleteEx] {

  type FormData

  val format: Form[CompExType]

  protected def unapplyCompEx(compEx: CompExType): Option[FormData]

}
