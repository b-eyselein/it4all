package initialData.sql

import initialData.InitialData.{lessonResourcesPath, loadTextFromFile}
import model.lesson.{Lesson, LessonContent, LessonTextContent}

object SqlLesson1 {

  private val toolId = "sql"

  private val lessonResPath = lessonResourcesPath(toolId, 1)

  val sqlLesson01: Lesson = Lesson(1, toolId, "Grundlagen SQL", "In dieser Lektion geht es um die Grundlagen von SQL.")

  val sqlLesson01Content: Seq[LessonContent] = Seq(
    LessonTextContent(1, 1, toolId, loadTextFromFile(lessonResPath / "content_01.html")),
    LessonTextContent(2, 1, toolId, loadTextFromFile(lessonResPath / "content_02.html"))
  )

}
