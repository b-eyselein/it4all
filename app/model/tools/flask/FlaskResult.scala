package model.tools.flask

import model.points.Points
import model.result.{AbstractCorrectionResult, InternalErrorResult}

trait FlaskAbstractResult extends AbstractCorrectionResult

final case class FlaskInternalErrorResult(
  msg: String,
  maxPoints: Points
) extends FlaskAbstractResult
    with InternalErrorResult

final case class FlaskResult(
  testResults: Seq[FlaskTestResult],
  points: Points,
  maxPoints: Points
) extends FlaskAbstractResult {

  override def isCompletelyCorrect: Boolean = testResults.forall(_.successful)

}

final case class FlaskTestResult(
  testId: Int,
  testName: String,
  successful: Boolean,
  stdout: Seq[String],
  stderr: Seq[String]
)
