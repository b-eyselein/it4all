package model

import play.api.libs.json._

final case class KeyValueObject(key: String, value: String)

object JsonProtocols {

  // Collections and exercises

  val levelForExerciseFormat: OFormat[LevelForExercise] = Json.format

  val userProficiencyFormat: OFormat[UserProficiency] = {
    implicit val x0: OFormat[Topic]            = Json.format
    implicit val x1: OFormat[LevelForExercise] = levelForExerciseFormat

    Json.format
  }

}
