package model

// Paragraph

sealed trait ExerciseTextParagraph

final case class ExerciseTextTextParagraph(
  textParts: Seq[TextPart]
) extends ExerciseTextParagraph

object ExerciseTextTextParagraph {

  def withParts(parts: TextPart*): ExerciseTextTextParagraph = ExerciseTextTextParagraph(parts)

}

final case class ExerciseTextListParagraph(
  numbered: Boolean = false,
  points: Seq[BulletListPoint]
) extends ExerciseTextParagraph

object ExerciseTextListParagraph {

  def apply(points: BulletListPoint*): ExerciseTextListParagraph = new ExerciseTextListParagraph(points = points)

}

// Text parts

sealed trait TextPart

final case class StringTextPart(
  text: String
) extends TextPart

final case class HighlightedTextPart(
  text: String
) extends TextPart

// Bullet list

final case class BulletListPoint(
  textParts: Seq[TextPart]
)

object BulletListPoint {

  def withParts(parts: TextPart*): BulletListPoint = new BulletListPoint(parts)

}
