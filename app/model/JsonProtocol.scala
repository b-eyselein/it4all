package model

import play.api.libs.json._

final case class KeyValueObject(key: String, value: String)

object JsonProtocols {

  // Collections and exercises

  private val topicFormat: OFormat[Topic] = Json.format

  val topicWithLevelFormat: OFormat[TopicWithLevel] = {
    implicit val tf: OFormat[Topic] = topicFormat

    Json.format
  }

  val levelForExerciseFormat: OFormat[LevelForExercise] = Json.format

  val userProficiencyFormat: OFormat[UserProficiency] = {
    implicit val x0: OFormat[Topic]            = topicFormat
    implicit val x1: OFormat[LevelForExercise] = levelForExerciseFormat

    Json.format
  }

}
