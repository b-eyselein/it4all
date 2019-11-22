package model

final case class ExerciseReview(
  difficulty: Difficulty,
  maybeDuration: Option[Int]
)
