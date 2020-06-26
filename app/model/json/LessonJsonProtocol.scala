package model.json

import model.lesson._
import play.api.libs.json._

trait LessonJsonProtocol {

  private val lessonTextContentFormat: OFormat[LessonTextContent] = Json.format

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

    implicit val ltcf: Format[LessonTextContent] = lessonTextContentFormat

    implicit val lqcf: Format[LessonMultipleChoiceQuestionsContent] = {
      implicit val quf: Format[LessonMultipleChoiceQuestionAnswer] = Json.format
      implicit val qf: Format[LessonMultipleChoiceQuestion]        = Json.format

      Json.format
    }

    Json.format
  }

  val lessonFormat: OFormat[Lesson] = Json.format

}
