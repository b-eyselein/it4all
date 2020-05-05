package model.json

import model._
import model.tools._
import play.api.libs.json._

final case class ReadCollectionsMessage(collections: Seq[ExerciseCollection])

final case class KeyValueObject(key: String, value: String)

object JsonProtocols extends LessonJsonProtocol {

  val topicFormat: OFormat[Topic] = Json.format

  val registerValuesFormat: OFormat[RegisterValues] = Json.format

  val userCredentialsFormat: OFormat[UserCredentials] = Json.format

  val loggedInUserFormat: OFormat[LoggedInUser] = Json.format

  val loggedInUserWithTokenFormat: OFormat[LoggedInUserWithToken] = {
    implicit val liuf: OFormat[LoggedInUser] = loggedInUserFormat

    Json.format
  }

  val collectionFormat: OFormat[ExerciseCollection] = Json.format

  val exerciseFileFormat: OFormat[ExerciseFile] = Json.format

  val readCollectionsMessageReads: Reads[ReadCollectionsMessage] = {
    implicit val cr: Reads[ExerciseCollection] = JsonProtocols.collectionFormat

    Json.reads
  }

  val userProficiencyFormat: OFormat[UserProficiency] = {
    implicit val tf: OFormat[Topic] = topicFormat

    Json.format
  }

}
