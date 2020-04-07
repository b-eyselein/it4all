package model.tools

import play.api.libs.json.JsValue

final case class SemanticVersion(major: Int, minor: Int, patch: Int)

final case class ExTag(abbreviation: String, title: String)

final case class Exercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  semanticVersion: SemanticVersion,
  title: String,
  authors: Seq[String],
  text: String,
  tags: Seq[ExTag],
  difficulty: Option[Int],
  // FIXME: use generic ExerciseContent?
  content: JsValue
) {

  def parts: Seq[ExPart] = Seq()

}

final case class SampleSolution[SolType](
  id: Int,
  sample: SolType
)

trait ExerciseContent {

  type SolType

  val sampleSolutions: Seq[SampleSolution[SolType]]

}

final case class ExerciseFile(
  name: String,
  resourcePath: String,
  fileType: String,
  editable: Boolean,
  content: String
)

trait StringExerciseContent extends ExerciseContent {

  override type SolType = String

}
