package model.tools.sql

import model.Difficulties
import model.core.ToolForms
import model.tools.sql.SqlConsts._
import play.api.data.Form
import play.api.data.Forms._

object SqlToolForms extends ToolForms[SqlExercise, SqlExerciseReview] {

  override val exerciseReviewForm: Form[SqlExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(SqlExerciseReview.apply)(SqlExerciseReview.unapply)
  )

}
