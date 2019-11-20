package model.tools.collectionTools.uml

import model.Difficulties
import model.core.ToolForms
import model.tools.collectionTools.uml.UmlConsts._
import play.api.data.Form
import play.api.data.Forms._

object UmlToolForms extends ToolForms[UmlExerciseReview] {

  override val exerciseReviewForm: Form[UmlExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(UmlExerciseReview.apply)(UmlExerciseReview.unapply)
  )

}
