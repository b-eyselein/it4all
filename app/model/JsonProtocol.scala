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

  val userProficiencyFormat: OFormat[UserProficiency] = {
    implicit val tf: OFormat[Topic]              = topicFormat
    implicit val lfef: OFormat[LevelForExercise] = Json.format

    Json.format
  }

}
