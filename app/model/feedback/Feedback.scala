package model.feedback

import model.Enums.{EvaluatedAspect, Mark}

import scala.collection.mutable.ListBuffer

object EvaluatedTool {
  var values: ListBuffer[EvaluatedTool] = ListBuffer.empty

  def byName(name: String): Option[EvaluatedTool] = values.find(_.toString == name)
}

abstract sealed class EvaluatedTool {
  EvaluatedTool.values += this
}

case object HtmlCss extends EvaluatedTool

case object JsWeb extends EvaluatedTool

case object Sql extends EvaluatedTool

case class Feedback(userName: String, tool: EvaluatedTool, var sense: Mark, var used: Mark, var usability: Mark, var feedback: Mark, var fairness: Mark, comment: String) {

  def get(evaledAspect: EvaluatedAspect): Mark = evaledAspect match {
    case EvaluatedAspect.FAIRNESS_OF_FEEDBACK => fairness
    case EvaluatedAspect.SENSE                => sense
    case EvaluatedAspect.STYLE_OF_FEEDBACK    => feedback
    case EvaluatedAspect.USABILITY            => usability
    case EvaluatedAspect.USED                 => used
  }

  def set(evaledAspect: EvaluatedAspect, mark: Mark): Unit = evaledAspect match {
    case EvaluatedAspect.FAIRNESS_OF_FEEDBACK => fairness = mark
    case EvaluatedAspect.SENSE                => sense = mark
    case EvaluatedAspect.STYLE_OF_FEEDBACK    => feedback = mark
    case EvaluatedAspect.USABILITY            => usability = mark
    case EvaluatedAspect.USED                 => used = mark
  }

}
