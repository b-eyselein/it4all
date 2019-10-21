package model

import play.api.libs.json.{Format, Json}

object JsonProtocol {

  final val collectionFormat: Format[ExerciseCollection] = Json.format[ExerciseCollection]

}
