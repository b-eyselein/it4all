package model.tools.regex

import model.core.result.{CompleteResult, CompleteResultJsonProtocol, EvaluationResult, SuccessType}
import model.points.Points
import model.tools.regex.BinaryClassificationResultTypes._
import model.tools.regex.RegexConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class RegexEvaluationResult(testData: RegexTestData, resultType: BinaryClassificationResultType) extends EvaluationResult {

  override def success: SuccessType = resultType match {
    case TruePositive | TrueNegative   => SuccessType.COMPLETE
    case FalsePositive | FalseNegative => SuccessType.NONE
  }

}

final case class RegexCompleteResult(learnerSolution: String, exercise: RegexExercise, part: RegexExPart,
                                     results: Seq[RegexEvaluationResult], points: Points, maxPoints: Points) extends CompleteResult[RegexEvaluationResult] {

  override type SolType = String

}

object RegexCompleteResultJsonProtocol extends CompleteResultJsonProtocol[RegexEvaluationResult, RegexCompleteResult] {

  override def completeResultWrites(solutionSaved: Boolean): Writes[RegexCompleteResult] = (
    (__ \ solutionSavedName).write[Boolean] and
      (__ \ solutionName).write[String] and
      (__ \ pointsName).write[Double] and
      (__ \ maxPointsName).write[Double] and
      (__ \ resultsName).write[Seq[RegexEvaluationResult]]
    ) (rcr => (solutionSaved, rcr.learnerSolution, rcr.points.asDouble, rcr.maxPoints.asDouble, rcr.results))

  private implicit val regexEvaluationResultWrites: Writes[RegexEvaluationResult] = (
    (__ \ testDataName).write[String] and
      (__ \ includedName).write[Boolean] and
      (__ \ resultTypeName).write[String]
    ) (rer => (rer.testData.data, rer.testData.isIncluded, rer.resultType.entryName))

}
