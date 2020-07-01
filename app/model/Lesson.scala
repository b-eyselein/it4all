package model

sealed trait LessonContent {
  val contentId: Int
  val lessonId: Int
  val toolId: String
}

// Text content

final case class LessonTextContent(
  contentId: Int,
  lessonId: Int,
  toolId: String,
  content: String
) extends LessonContent

// MC question content

final case class LessonMultipleChoiceQuestionAnswer(
  id: Int,
  answer: String,
  isCorrect: Boolean
)

final case class LessonMultipleChoiceQuestion(
  id: Int,
  questionText: String,
  answers: Seq[LessonMultipleChoiceQuestionAnswer]
)

final case class LessonMultipleChoiceQuestionsContent(
  contentId: Int,
  lessonId: Int,
  toolId: String,
  questions: Seq[LessonMultipleChoiceQuestion]
) extends LessonContent

// Lesson

final case class Lesson(
  lessonId: Int,
  toolId: String,
  title: String,
  description: String
)
