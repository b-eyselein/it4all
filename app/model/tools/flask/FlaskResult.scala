package model.tools.flask

import model.AbstractCorrectionResult
import model.points.Points

final case class FlaskResult(
  testResults: Seq[FlaskTestResult],
  points: Points,
  maxPoints: Points
) extends AbstractCorrectionResult {

  override def isCompletelyCorrect: Boolean = testResults.forall(_.successful)

}

final case class FlaskTestResult(
  testId: Int,
  testName: String,
  successful: Boolean,
  stdout: Seq[String],
  stderr: Seq[String]
)
