package model.tools.collectionTools.web

import model._
import model.core.ToolForms
import model.tools.collectionTools.web.WebConsts._
import play.api.data.Form
import play.api.data.Forms._

object WebToolForms extends ToolForms[WebExercise, WebExerciseReview] {

  override val exerciseReviewForm: Form[WebExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(WebExerciseReview.apply)(WebExerciseReview.unapply)
  )

}
