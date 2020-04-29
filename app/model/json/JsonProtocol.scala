package model.json

import model.User
import model.tools._
import play.api.libs.json._

final case class ReadCollectionsMessage(collections: Seq[ExerciseCollection])

final case class KeyValueObject(key: String, value: String)

object JsonProtocols extends LessonJsonProtocol {

  val userFormat: OFormat[User] = Json.format

  val collectionFormat: OFormat[ExerciseCollection] = Json.format

  val exerciseFileFormat: OFormat[ExerciseFile] = Json.format

  val readCollectionsMessageReads: Reads[ReadCollectionsMessage] = {
    implicit val cr: Reads[ExerciseCollection] = JsonProtocols.collectionFormat

    Json.reads
  }

}
