package model.core

import model.{Exercise, ExerciseCollection, ExerciseReview}
import play.api.data.Form

trait ToolForms[ExType <: Exercise, CollType <: ExerciseCollection, ReviewType <: ExerciseReview] {

  val collectionFormat: Form[CollType]

  val exerciseFormat: Form[ExType]

  val exerciseReviewForm: Form[ReviewType]

}
