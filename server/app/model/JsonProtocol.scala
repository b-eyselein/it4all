package model

import play.api.libs.json.{Format, Json}

object JsonProtocol {

  val collectionFormat: Format[ExerciseCollection] = Json.format[ExerciseCollection]

  val exerciseBasicsFormat: Format[ApiExerciseBasics] = {
    implicit val svf: Format[SemanticVersion] = SemanticVersionHelper.format

    Json.format[ApiExerciseBasics]
  }


  val userFormat: Format[User] = {
    implicit val ltiUserFormat: Format[LtiUser] = Json.format[LtiUser]

    implicit val registeredUserFormat: Format[RegisteredUser] = Json.format[RegisteredUser]

    Json.format[User]
  }

}
