package model.tools.collectionTools.rose

import model.core.ToolForms
import model.tools.collectionTools.programming.{ProgDataType, ProgDataTypes, ProgLanguages}
import model.tools.collectionTools.rose.RoseConsts._
import model.{Difficulties, ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object RoseToolForms extends ToolForms[RoseExerciseReview] {

  override val exerciseReviewForm: Form[RoseExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(RoseExerciseReview.apply)(RoseExerciseReview.unapply)
  )

}
