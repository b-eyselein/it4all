package model.tools

import model._
import play.api.libs.json._

trait ToolJsonProtocol[S, C <: ExerciseContent, PartType <: ExPart] {

  protected val keyValueObjectMapFormat: Format[Map[String, String]] = {

    val keyValueObjectFormat: Format[KeyValueObject] = Json.format[KeyValueObject]

    Format(
      Reads.seq(keyValueObjectFormat).map(_.map { case KeyValueObject(key, value) => (key, value) }.toMap),
      Writes.seq(keyValueObjectFormat).contramap(_.toSeq.map { case (key, value) => KeyValueObject(key, value) })
    )
  }

  val partTypeFormat: Format[PartType]

  val solutionFormat: Format[S]

  lazy val sampleSolutionFormat: OFormat[SampleSolution[S]] = {
    implicit val sf: Format[S] = solutionFormat

    Json.format
  }

  lazy val userSolutionFormat: OFormat[UserSolution[S, PartType]] = {
    implicit val ptf: Format[PartType] = partTypeFormat
    implicit val sf: Format[S]         = solutionFormat

    Json.format
  }

  val exerciseContentFormat: OFormat[C]

  final lazy val exerciseFormat: OFormat[Exercise[C]] = {
    implicit val twlf: OFormat[TopicWithLevel] = JsonProtocols.topicWithLevelFormat
    implicit val fc: OFormat[C]                = exerciseContentFormat

    Json.format
  }

}

abstract class StringSampleSolutionToolJsonProtocol[C <: ExerciseContent, PartType <: ExPart]
    extends ToolJsonProtocol[String, C, PartType] {

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

}
