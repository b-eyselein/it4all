package model

final case class ExerciseText(
  heading: Option[String],
  parts: Seq[ExerciseTextParagraph]
)

// Paragraph

sealed trait ExerciseTextParagraph

final case class ExerciseTextTextParagraph(
  textParts: Seq[ExerciseTextPart]
) extends ExerciseTextParagraph

final case class ExerciseTextListParagraph(
) extends ExerciseTextParagraph

// Text parts

final case class ExerciseTextPart(
  text: String
)
