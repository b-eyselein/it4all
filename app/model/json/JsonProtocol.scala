package model.json

import model.User
import model.adaption.{Proficiencies, ToolProficiency, TopicProficiency}
import model.tools._
import play.api.libs.json._

final case class ReadCollectionsMessage(collections: Seq[ExerciseCollection])

final case class KeyValueObject(key: String, value: String)

object JsonProtocols extends LessonJsonProtocol {

  val userFormat: Format[User] = Json.format

  val proficienciesFormat: Format[Proficiencies] = {
    implicit val toolProficiencyFormat: Format[ToolProficiency]   = Json.format
    implicit val topicProficiencyFormat: Format[TopicProficiency] = Json.format

    Json.format
  }

  val collectionFormat: Format[ExerciseCollection] = Json.format

  val topicFormat: Format[Topic] = Json.format

  val exerciseFileFormat: Format[ExerciseFile] = Json.format

  val readCollectionsMessageReads: Reads[ReadCollectionsMessage] = {
    implicit val cr: Reads[ExerciseCollection] = JsonProtocols.collectionFormat

    Json.reads
  }

}
