package model.tools.flask

import model._

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

  def maxPoints: Int = testConfig.tests.map(_.maxPoints).sum

}
