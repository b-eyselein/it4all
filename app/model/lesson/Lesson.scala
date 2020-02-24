package model.lesson

sealed trait LessonContent {
  val id: Int
  val lessonId: Int
  val toolId: String
}

final case class LessonTextContent(
  id: Int,
  lessonId: Int,
  toolId: String,
  content: String
) extends LessonContent

final case class QuestionAnswer(
  answer: String,
  isCorrect: Boolean
)

final case class Question(
  id: Int,
  question: String,
  answers: Seq[QuestionAnswer]
)

final case class LessonQuestionsContent(
  id: Int,
  lessonId: Int,
  toolId: String,
  questions: Seq[Question]
) extends LessonContent

final case class Lesson(
  id: Int,
  toolId: String,
  title: String,
  content: Seq[LessonContent]
)
