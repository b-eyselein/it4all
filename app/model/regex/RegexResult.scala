package model.regex

import model.regex.RegexConsts._
import model.core.result.{CompleteResult, CompleteResultJsonProtocol, EvaluationResult, SuccessType}
import play.api.libs.json._
import play.api.libs.functional.syntax._

final case class RegexEvaluationResult(testData: RegexTestData, resultType: BinaryClassificationResultType) extends EvaluationResult {

  override def success: SuccessType = ???

}


final case class RegexCompleteResult(learnerSolution: String, exercise: RegexCompleteEx, part: RegexExPart,
                                     results: Seq[RegexEvaluationResult]) extends CompleteResult[RegexEvaluationResult] {

  override type SolType = String

}

object RegexCompleteResultJsonProtocol extends CompleteResultJsonProtocol[RegexEvaluationResult, RegexCompleteResult] {

  override def completeResultWrites(solutionSaved: Boolean): Writes[RegexCompleteResult] = (
    (__ \ solutionSavedName).write[Boolean] and
      (__ \ solutionName).write[String] and
      (__ \ resultsName).write[Seq[RegexEvaluationResult]]
    ) (rcr => (solutionSaved, rcr.learnerSolution, rcr.results))

  private implicit val regexEvaluationResultWrites: Writes[RegexEvaluationResult] = (
    (__ \ testDataName).write[String] and
      (__ \ includedName).write[Boolean] and
      (__ \ resultTypeName).write[String]
    ) (rer => (rer.testData.data, rer.testData.isIncluded, rer.resultType.entryName))

}