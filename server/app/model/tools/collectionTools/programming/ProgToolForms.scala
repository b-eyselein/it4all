package model.tools.collectionTools.programming

import model.Difficulties
import model.core.ToolForms
import model.tools.collectionTools.programming.ProgConsts._
import play.api.data.Form
import play.api.data.Forms._

object ProgToolForms extends ToolForms[ProgExercise, ProgExerciseReview] {

  override val exerciseReviewForm: Form[ProgExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(ProgExerciseReview.apply)(ProgExerciseReview.unapply)
  )

}
