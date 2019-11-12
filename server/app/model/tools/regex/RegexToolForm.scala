package model.tools.regex

import model.Difficulties
import model.core.ToolForms
import model.tools.regex.RegexConsts._
import play.api.data.Form
import play.api.data.Forms._

object RegexToolForm extends ToolForms[RegexExercise, RegexExerciseReview] {

  override val exerciseReviewForm: Form[RegexExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(RegexExerciseReview.apply)(RegexExerciseReview.unapply)
  )

}
