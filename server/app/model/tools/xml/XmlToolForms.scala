package model.tools.xml

import model.Difficulties
import model.core.ToolForms
import model.tools.xml.XmlConsts._
import play.api.data.Form
import play.api.data.Forms._

object XmlToolForms extends ToolForms[XmlExercise, XmlExerciseReview] {

  override val exerciseReviewForm: Form[XmlExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(XmlExerciseReview.apply)(XmlExerciseReview.unapply)
  )

}
