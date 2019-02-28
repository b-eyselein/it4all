package model.core

import model.{Exercise, ExerciseCollection}
import play.api.data.Form

trait ExerciseForm[ExType <: Exercise, CollType <: ExerciseCollection] {

  val exerciseFormat: Form[ExType]

  val collectionFormat: Form[CollType]

}
