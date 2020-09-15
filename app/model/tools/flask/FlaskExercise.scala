package model.tools.flask

import enumeratum.PlayEnum
import model._
import model.points.Points
import model.result.{AbstractCorrectionResult, InternalErrorResult}

sealed abstract class FlaskExPart(
  override val partName: String,
  override val id: String
) extends ExPart

object FlaskExPart extends PlayEnum[FlaskExPart] {

  val values: IndexedSeq[FlaskExPart] = findValues

  case object FlaskSingleExPart extends FlaskExPart(partName = "Server erstellen", id = "solve")

}

final case class FlaskExerciseContent(
  files: Seq[ExerciseFile],
  maxPoints: Int,
  override val sampleSolutions: Seq[SampleSolution[FilesSolution]]
) extends ExerciseContent {

  override protected type S = FilesSolution

  override def parts: Seq[ExPart] = Seq(FlaskExPart.FlaskSingleExPart)

}

trait FlaskAbstractCorrectionResult extends AbstractCorrectionResult

final case class FlaskInternalErrorResult(
  msg: String,
  maxPoints: Points
) extends FlaskAbstractCorrectionResult
    with InternalErrorResult

final case class FlaskCorrectionResult(
  points: Points,
  maxPoints: Points
) extends FlaskAbstractCorrectionResult {
  override def isCompletelyCorrect: Boolean = ???
}
