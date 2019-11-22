package model

import model.tools.collectionTools.{ApiExerciseBasics, ExerciseCollection}
import play.api.libs.json.{Format, Json}

object JsonProtocol {

  val userFormat: Format[User] = {
    implicit val ltiUserFormat: Format[LtiUser] = Json.format[LtiUser]

    implicit val registeredUserFormat: Format[RegisteredUser] = Json.format[RegisteredUser]

    Json.format[User]
  }

}
