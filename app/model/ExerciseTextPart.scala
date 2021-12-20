package model

final case class ExerciseText(
  heading: Option[String],
  parts: Seq[ExerciseTextParagraph]
)

final case class ExerciseTextParagraph(
)

sealed trait ExerciseTextPart
