package model.tools.rose

import model.core.ToolForms
import model.tools.programming.{ProgDataType, ProgDataTypes, ProgLanguages}
import model.tools.rose.RoseConsts._
import model.{Difficulties, ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object RoseToolForms extends ToolForms[RoseExercise, RoseExerciseReview] {

  override val exerciseReviewForm: Form[RoseExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(RoseExerciseReview.apply)(RoseExerciseReview.unapply)
  )

}
