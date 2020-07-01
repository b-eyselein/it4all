package initialData.sql

import initialData.InitialData.{lessonResourcesPath, loadTextFromFile}
import model._

object SqlLesson1 {

  private val toolId = "sql"

  private val lessonResPath = lessonResourcesPath(toolId, 1)

  val sqlLesson01: Lesson = Lesson(1, toolId, "Grundlagen SQL", "In dieser Lektion geht es um die Grundlagen von SQL.")

  private val content02Questions = Seq(
    LessonMultipleChoiceQuestion(
      1,
      "Kreuzen Sie Antwort 'X' an.",
      Seq(
        LessonMultipleChoiceQuestionAnswer(1, "X", isCorrect = true),
        LessonMultipleChoiceQuestionAnswer(2, "Y", isCorrect = false),
        LessonMultipleChoiceQuestionAnswer(3, "Z", isCorrect = false)
      )
    ),
    LessonMultipleChoiceQuestion(
      2,
      "Kreuzen Sie Antwort 'Y' an.",
      Seq(
        LessonMultipleChoiceQuestionAnswer(1, "X", isCorrect = false),
        LessonMultipleChoiceQuestionAnswer(2, "Y", isCorrect = true),
        LessonMultipleChoiceQuestionAnswer(3, "Z", isCorrect = false)
      )
    )
  )

  val sqlLesson01Content: Seq[LessonContent] = Seq(
    LessonTextContent(1, 1, toolId, loadTextFromFile(lessonResPath / "content_01.html")),
    LessonMultipleChoiceQuestionsContent(2, 1, toolId, content02Questions),
    LessonTextContent(3, 1, toolId, loadTextFromFile(lessonResPath / "content_02.html")),
    LessonMultipleChoiceQuestionsContent(4, 1, toolId, Seq())
  )

}
