package model.tools.collectionTools.xml

import model.Difficulties
import model.core.ToolForms
import model.tools.collectionTools.xml.XmlConsts._
import play.api.data.Form
import play.api.data.Forms._

object XmlToolForms extends ToolForms[XmlExerciseReview] {

  override val exerciseReviewForm: Form[XmlExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(XmlExerciseReview.apply)(XmlExerciseReview.unapply)
  )

}
