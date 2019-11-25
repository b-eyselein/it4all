package model.tools.collectionTools

import model.SemanticVersion

final case class BaseValues(
  id: Int,
  collId: Int,
  semanticVersion: SemanticVersion,
  title: String,

  author: String,
  text: String,
)

final case class ApiExerciseBasics(
  id: Int,
  collId: Int,
  toolId: String,
  semanticVersion: SemanticVersion,
  title: String
)
