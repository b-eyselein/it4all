package initialData.sql

import initialData.InitialData.{lessonResourcesPath, loadTextFromFile}
import model._

object SqlLessons {

  private val toolId = "sql"

  private val lessonResPath = lessonResourcesPath(toolId, 1)

  val sqlLesson01: Lesson = Lesson(
    1,
    toolId,
    "Grundlagen SQL - Teil 1",
    "In dieser Lektion geht es um die Grundlagen von SQL.",
    video = Some("https://www.youtube.com/embed/fgOiWEGNJ-o")
  )

  val sqlLesson01Content: Seq[LessonContent] = Seq(
    LessonTextContent(1, 1, toolId, loadTextFromFile(lessonResPath / "content_01.html")),
    LessonMultipleChoiceQuestionsContent(
      2,
      1,
      toolId,
      Seq(
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
    ),
    LessonTextContent(3, 1, toolId, loadTextFromFile(lessonResPath / "content_02.html")),
    LessonMultipleChoiceQuestionsContent(4, 1, toolId, Seq())
  )

  val sqlLesson02: Lesson = Lesson(
    2,
    toolId,
    "Grundlagen SQL - Teil 2",
    "In dieser Lektionen geht es auch um die Grundlagen von SQL.",
    video = None
  )

  val sqlLesson02Content: Seq[LessonContent] = Seq()

}
