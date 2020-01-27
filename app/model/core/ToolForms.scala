package model.core

import model._
import model.core.CoreConsts._
import play.api.data.Form
import play.api.data.Forms._

object ToolForms {

  val exerciseReviewForm: Form[ExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(ExerciseReview.apply)(ExerciseReview.unapply)
  )


}
