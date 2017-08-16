package model;

import scala.collection.JavaConverters._

import model.exercise.Success;
import model.result.EvaluationResult;
import play.api.libs.json._
import play.api.libs.functional.syntax._

class BooleanQuestionResult(success: Success, val learnerSolution: String, question: CreationQuestion) extends EvaluationResult(success) {

  def getAsHtml() = messages.asScala.mkString(",")

  def getSolutions() = if (question == null) List.empty else question.solutions

}