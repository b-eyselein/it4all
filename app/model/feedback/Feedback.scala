package model.feedback

import model.core.CoreConsts._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.JsValue

case object FeedbackFormHelper {

  private def forFormUnapplied(
    arg: Feedback
  ): Option[(Option[String], Option[String], Option[Int], Mark, Mark, Mark, Mark, Mark, String)] = Some(
    (
      arg.targetDegree,
      arg.subject,
      arg.semester,
      arg.getMarkForAspect(EvaluatedAspects.SENSE),
      arg.getMarkForAspect(EvaluatedAspects.USED),
      arg.getMarkForAspect(EvaluatedAspects.USABILITY),
      arg.getMarkForAspect(EvaluatedAspects.STYLE_OF_FEEDBACK),
      arg.getMarkForAspect(EvaluatedAspects.FAIRNESS_OF_FEEDBACK),
      arg.comment
    )
  )

  private def fromFormApplied(
    targetDegree: Option[String],
    subject: Option[String],
    semester: Option[Int],
    sense: Mark,
    used: Mark,
    usability: Mark,
    feedback: Mark,
    fairness: Mark,
    comment: String
  ) = Feedback(
    targetDegree,
    subject,
    semester,
    Map(
      EvaluatedAspects.SENSE                -> sense,
      EvaluatedAspects.USED                 -> used,
      EvaluatedAspects.USABILITY            -> usability,
      EvaluatedAspects.STYLE_OF_FEEDBACK    -> feedback,
      EvaluatedAspects.FAIRNESS_OF_FEEDBACK -> fairness
    ),
    comment
  )

  val feedbackFormMapping: Form[Feedback] = Form(
    mapping(
      targetDegreeName       -> optional(nonEmptyText),
      subjectName            -> optional(nonEmptyText),
      semesterName           -> optional(number),
      "sense"                -> Mark.formField,
      "used"                 -> Mark.formField,
      "usability"            -> Mark.formField,
      "style_of_feedback"    -> Mark.formField,
      "fairness_of_feedback" -> Mark.formField,
      "comment"              -> text
    )(fromFormApplied)(forFormUnapplied)
  )

}

final case class Feedback(
  targetDegree: Option[String] = None,
  subject: Option[String] = None,
  semester: Option[Int] = None,
  marks: Map[EvaluatedAspect, Mark] = Map.empty,
  comment: String = ""
) {

  def marksJson: JsValue = ???

  def getMarkForAspect(aspect: EvaluatedAspect): Mark = marks.getOrElse(aspect, Mark.NoMark)

}
