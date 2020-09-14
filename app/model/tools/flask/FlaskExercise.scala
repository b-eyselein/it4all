package model.tools.flask

import enumeratum.PlayEnum
import model.points.Points
import model.result.AbstractCorrectionResult
import model.{ExPart, ExerciseContent, FilesSolution, SampleSolution}

sealed abstract class FlaskExPart(
  override val partName: String,
  override val id: String
) extends ExPart

object FlaskExPart extends PlayEnum[FlaskExPart] {

  val values: IndexedSeq[FlaskExPart] = findValues

  case object FlaskSingleExPart extends FlaskExPart(partName = "Server erstellen", id = "solve")

}

final case class FlaskExerciseContent(
  override val sampleSolutions: Seq[SampleSolution[FilesSolution]]
) extends ExerciseContent {

  override protected type S = FilesSolution

  override def parts: Seq[ExPart] = ???

}

final case class FlaskCorrectionResult(
  points: Points,
  maxPoints: Points
) extends AbstractCorrectionResult {
  override def isCompletelyCorrect: Boolean = ???
}
