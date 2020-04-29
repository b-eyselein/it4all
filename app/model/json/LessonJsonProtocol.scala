package model.json

import model.lesson._
import play.api.libs.json._

trait LessonJsonProtocol {

  val lessonContentFormat: OFormat[LessonContent] = {

    implicit val cfg: JsonConfiguration = JsonConfiguration(
      typeNaming = JsonNaming { fullName =>
        // FIXME: match complete string!?!
        fullName.split("\\.").lastOption match {
          case Some("LessonTextContent")      => "Text"
          case Some("LessonQuestionsContent") => "Questions"
          case _                              => fullName
        }
      }
    )

    implicit val ltcf: Format[LessonTextContent] = Json.format
    implicit val lqcf: Format[LessonQuestionsContent] = {
      implicit val quf: Format[QuestionAnswer] = Json.format
      implicit val qf: Format[Question]        = Json.format

      Json.format
    }

    Json.format
  }

  val lessonFormat: OFormat[Lesson] = {
    implicit val lcf: OFormat[LessonContent] = lessonContentFormat

    Json.format
  }

}
