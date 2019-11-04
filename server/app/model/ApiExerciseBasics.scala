package model

final case class ApiExerciseBasics(
  id: Int,
  collId: Int,
  semanticVersion: SemanticVersion,
  title: String
)
