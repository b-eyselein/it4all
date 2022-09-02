package model.tools.flask

import enumeratum.PlayEnum
import model._

sealed abstract class FlaskExPart(
  override val partName: String,
  override val id: String
) extends ExPart

object FlaskExPart extends PlayEnum[FlaskExPart] {

  val values: IndexedSeq[FlaskExPart] = findValues

  case object FlaskSingleExPart extends FlaskExPart(partName = "Server erstellen", id = "solve")

}

final case class FlaskSingleTestConfig(
  id: Int,
  description: String,
  maxPoints: Int,
  testName: String,
  dependencies: Option[Seq[String]] = None
)

final case class FlaskTestsConfig(
  testFileName: String,
  testClassName: String,
  tests: Seq[FlaskSingleTestConfig]
)

final case class FlaskExerciseContent(
  files: Seq[ExerciseFile],
  testFiles: Seq[ExerciseFile],
  testConfig: FlaskTestsConfig,
  override val sampleSolutions: Seq[FilesSolution]
) extends FileExerciseContent {

  override def parts: Seq[ExPart] = Seq(FlaskExPart.FlaskSingleExPart)

  def maxPoints: Int = testConfig.tests.map(_.maxPoints).sum

}
