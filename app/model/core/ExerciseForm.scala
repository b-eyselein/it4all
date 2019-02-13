package model.core

import model.Exercise
import play.api.data.Form

trait ExerciseForm[CompExType <: Exercise] {

  type FormData

  val format: Form[CompExType]

  protected def unapplyCompEx(compEx: CompExType): Option[FormData]

}
