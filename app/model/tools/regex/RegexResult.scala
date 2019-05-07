package model.tools.regex

import model.core.result.{CompleteResult, CompleteResultJsonProtocol, EvaluationResult, SuccessType}
import model.points._
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

final case class RegexCompleteResult(results: Seq[RegexEvaluationResult], points: Points, maxPoints: Points, solutionSaved: Boolean = false)
  extends CompleteResult[RegexEvaluationResult]


object RegexCompleteResultJsonProtocol extends CompleteResultJsonProtocol[RegexEvaluationResult, RegexCompleteResult] {

  private implicit val pointsWrites = pointsJsonWrites

  private implicit val regexEvaluationResultWrites: Writes[RegexEvaluationResult] = (
    (__ \ testDataName).write[String] and
      (__ \ includedName).write[Boolean] and
      (__ \ resultTypeName).write[String]
    ) (rer => (rer.testData.data, rer.testData.isIncluded, rer.resultType.entryName))

  override val completeResultWrites: Writes[RegexCompleteResult] = Json.writes[RegexCompleteResult]

}
