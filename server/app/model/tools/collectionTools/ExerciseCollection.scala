package model.tools.collectionTools

import model.ExerciseState


final case class ExerciseCollection(
  id: Int,
  toolId: String,
  title: String,
  author: String,
  text: String,
  state: ExerciseState,
  shortName: String
)
