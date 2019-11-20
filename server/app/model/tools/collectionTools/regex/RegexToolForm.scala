package model.tools.collectionTools.regex

import model.Difficulties
import model.core.ToolForms
import model.tools.collectionTools.regex.RegexConsts._
import play.api.data.Form
import play.api.data.Forms._

object RegexToolForm extends ToolForms[ RegexExerciseReview] {

  override val exerciseReviewForm: Form[RegexExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(RegexExerciseReview.apply)(RegexExerciseReview.unapply)
  )

}
