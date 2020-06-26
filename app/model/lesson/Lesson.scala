package model.lesson

sealed trait LessonContent {
  val contentId: Int
  val lessonId: Int
  val toolId: String
}

final case class LessonTextContent(
  contentId: Int,
  lessonId: Int,
  toolId: String,
  content: String
) extends LessonContent

final case class LessonMultipleChoiceQuestionAnswer(
  answer: String,
  isCorrect: Boolean
)

final case class LessonMultipleChoiceQuestion(
  id: Int,
  question: String,
  answers: Seq[LessonMultipleChoiceQuestionAnswer]
)

final case class LessonMultipleChoiceQuestionsContent(
  contentId: Int,
  lessonId: Int,
  toolId: String,
  questions: Seq[LessonMultipleChoiceQuestion]
) extends LessonContent

final case class Lesson(
  lessonId: Int,
  toolId: String,
  title: String,
  description: String
)
