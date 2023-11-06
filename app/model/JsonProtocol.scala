package model

import play.api.libs.json._

import scala.annotation.unused

final case class KeyValueObject(key: String, value: String)

object JsonProtocols {

  // Collections and exercises

  val levelForExerciseFormat: OFormat[LevelForExercise] = Json.format

  val userProficiencyFormat: OFormat[UserProficiency] = {
    @unused implicit val topicFormat: OFormat[Topic]   = Json.format
    @unused implicit val x1: OFormat[LevelForExercise] = levelForExerciseFormat

    Json.format
  }

}
