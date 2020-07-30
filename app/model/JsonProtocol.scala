package model

import model.points.Points
import model.result.BasicExercisePartResult
import play.api.libs.json._

final case class KeyValueObject(key: String, value: String)

object JsonProtocols {

  // Lessons

  private val lessonTextContentFormat: OFormat[LessonTextContent] = Json.format

  private val lessonMultipleChoiceQuestionsContentFormat: OFormat[LessonMultipleChoiceQuestionsContent] = {
    implicit val quf: Format[LessonMultipleChoiceQuestionAnswer] = Json.format
    implicit val qf: Format[LessonMultipleChoiceQuestion]        = Json.format

    Json.format
  }

  val lessonContentFormat: OFormat[LessonContent] = {
    implicit val ltcf: Format[LessonTextContent]                    = lessonTextContentFormat
    implicit val lqcf: Format[LessonMultipleChoiceQuestionsContent] = lessonMultipleChoiceQuestionsContentFormat

    Json.format
  }

  val lessonFormat: OFormat[Lesson] = Json.format

  // User management

  val registerValuesFormat: OFormat[RegisterValues] = Json.format

  val userCredentialsFormat: OFormat[UserCredentials] = Json.format

  val loggedInUserFormat: OFormat[LoggedInUser] = Json.format

  val loggedInUserWithTokenFormat: OFormat[LoggedInUserWithToken] = {
    implicit val liuf: OFormat[LoggedInUser] = loggedInUserFormat

    Json.format
  }

  // Collections and exercises

  val topicFormat: OFormat[Topic] = Json.format

  val topicWithLevelFormat: OFormat[TopicWithLevel] = {
    implicit val tf: OFormat[Topic] = topicFormat

    Json.format
  }

  val exerciseCollectionFormat: OFormat[ExerciseCollection] = Json.format

  val exerciseFileFormat: OFormat[ExerciseFile] = Json.format

  val userProficiencyFormat: OFormat[UserProficiency] = {
    implicit val tf: OFormat[Topic]              = topicFormat
    implicit val lfef: OFormat[LevelForExercise] = Json.format

    Json.format
  }

  val basicExerciseResultFormat: OFormat[BasicExercisePartResult] = {
    implicit val pf: Format[Points] = Json.format

    Json.format
  }

}
