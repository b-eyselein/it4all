package model

final case class BaseValues(
  id: Int,
  collId: Int,
  semanticVersion: SemanticVersion,
  title: String,

  author: String,
  text: String,
  state: ExerciseState
)

final case class ApiExerciseBasics(
  id: Int,
  collId: Int,
  toolId: String,
  semanticVersion: SemanticVersion,
  title: String
)
