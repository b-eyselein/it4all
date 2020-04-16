package model.tools.sql

import model.json.JsonProtocols
import model.tools.{ReadExercisesMessage, SampleSolution, StringSampleSolutionToolJsonProtocol, Topic}
import play.api.libs.json._

object SqlJsonProtocols extends StringSampleSolutionToolJsonProtocol[SqlExercise, SqlExPart] {

  override val exerciseFormat: Format[SqlExercise] = {
    implicit val tf: Format[Topic]                    = JsonProtocols.topicFormat
    implicit val sssf: Format[SampleSolution[String]] = sampleSolutionFormat

    Json.format
  }

  override val partTypeFormat: Format[SqlExPart] = SqlExParts.jsonFormat

  override val readExercisesMessageReads: Reads[ReadExercisesMessage[SqlExercise]] = {
    implicit val ef: Format[SqlExercise] = exerciseFormat

    Json.reads
  }

}
