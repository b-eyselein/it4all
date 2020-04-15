package model.tools

final case class SemanticVersion(major: Int, minor: Int, patch: Int)

final case class ExTag(abbreviation: String, title: String)

trait Exercise {
  type ET
  type SolType

  val id: Int
  val collectionId: Int
  val toolId: String
  val semanticVersion: SemanticVersion

  val title: String
  val authors: Seq[String]
  val text: String
  val tags: Seq[ET]
  val difficulty: Option[Int]

  val sampleSolutions: Seq[SampleSolution[SolType]]
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
  fileType: String,
  editable: Boolean,
  content: String
)

trait StringExerciseContent extends ExerciseContent {

  override type SolType = String

}
