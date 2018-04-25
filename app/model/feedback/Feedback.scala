package model.feedback

import model.enums.EvaluatedAspects._
import model.enums.Mark.NO_MARK
import model.enums._
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}

case class FeedbackFormHelper(username: String, toolUrlPart: String) {

  protected implicit object MarkFormatter extends Formatter[Mark] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Mark] = data.get(key) match {
      case None           => Left(Seq(FormError(key, "No value found!")))
      case Some(valueStr) => Mark.withNameInsensitiveOption(valueStr) match {
        case Some(state) => Right(state)
        case None        => Left(Seq(FormError(key, s"Value '$valueStr' is no legal value!")))
      }
    }

    override def unbind(key: String, value: Mark): Map[String, String] = Map(key -> value.entryName)

  }

  val feedbackFormMapping = Form(
    mapping(
      "sense" -> Mark.formField,
      "used" -> Mark.formField,
      "usability" -> Mark.formField,
      "style_of_feedback" -> Mark.formField,
      "fairness_of_feedback" -> Mark.formField,
      "comment" -> text
    )(fromFormApplied)(forFormUnapplied)
  )

  private def fromFormApplied(sense: Mark, used: Mark, usability: Mark, feedback: Mark, fairness: Mark, comment: String) =
    Feedback(username, toolUrlPart, FeedbackTableHelper.constructList(sense, used, usability, feedback, fairness), comment)

  private def forFormUnapplied(arg: Feedback): Option[(Mark, Mark, Mark, Mark, Mark, String)] =
    Some((arg.getMarkForAspect(SENSE), arg.getMarkForAspect(USED), arg.getMarkForAspect(USABILITY),
      arg.getMarkForAspect(STYLE_OF_FEEDBACK), arg.getMarkForAspect(FAIRNESS_OF_FEEDBACK), arg.comment))

}

object FeedbackTableHelper {

  def constructList(sense: Mark, used: Mark, usability: Mark, feedback: Mark, fairness: Mark): Map[EvaluatedAspect, Mark] =
    Map(SENSE -> sense, USED -> used, USABILITY -> usability, STYLE_OF_FEEDBACK -> feedback, FAIRNESS_OF_FEEDBACK -> fairness)

  def forTableUnapplied(arg: Feedback): Option[(String, String, Mark, Mark, Mark, Mark, Mark, String)] =
    Some((arg.userName, arg.toolUrlPart, arg.getMarkForAspect(SENSE), arg.used, arg.usability, arg.feedback, arg.fairness, arg.comment))

  def fromTableTupled(values: (String, String, Mark, Mark, Mark, Mark, Mark, String)): Feedback =
    Feedback(values._1, values._2, constructList(values._3, values._4, values._5, values._6, values._7), values._8)

}

case class Feedback(userName: String, toolUrlPart: String, marks: Map[EvaluatedAspect, Mark] = Map.empty, comment: String = "") {

  def getMarkForAspect(aspect: EvaluatedAspect): Mark = marks.getOrElse(aspect, NO_MARK)

  def sense: Mark = getMarkForAspect(SENSE)

  def used: Mark = getMarkForAspect(USED)

  def usability: Mark = getMarkForAspect(USABILITY)

  def feedback: Mark = getMarkForAspect(STYLE_OF_FEEDBACK)

  def fairness: Mark = getMarkForAspect(FAIRNESS_OF_FEEDBACK)

}
