package model.tools

import model._
import model.points.Points
import model.result.BasicExerciseResult
import play.api.libs.json._

trait ToolJsonProtocol[S, C <: ExerciseContent, P <: ExPart] {

  protected val keyValueObjectMapFormat: Format[Map[String, String]] = {

    val keyValueObjectFormat: Format[KeyValueObject] = Json.format[KeyValueObject]

    Format(
      Reads.seq(keyValueObjectFormat).map(_.map { case KeyValueObject(key, value) => (key, value) }.toMap),
      Writes.seq(keyValueObjectFormat).contramap(_.toSeq.map { case (key, value) => KeyValueObject(key, value) })
    )
  }

  val partTypeFormat: Format[P]

  val solutionFormat: Format[S]

  lazy val sampleSolutionFormat: OFormat[SampleSolution[S]] = {
    implicit val sf: Format[S] = solutionFormat

    Json.format
  }

  lazy val userSolutionFormat: OFormat[UserSolution[S, P]] = {
    implicit val ptf: Format[P] = partTypeFormat
    implicit val sf: Format[S]  = solutionFormat

    Json.format
  }

  val exerciseContentFormat: OFormat[C]

  final lazy val exerciseFormat: OFormat[Exercise[C]] = {
    implicit val twlf: OFormat[TopicWithLevel] = JsonProtocols.topicWithLevelFormat
    implicit val fc: OFormat[C]                = exerciseContentFormat

    Json.format
  }

  final lazy val basicExerciseResultFormat: OFormat[BasicExerciseResult[P]] = {
    implicit val pt: Format[P]      = partTypeFormat
    implicit val pf: Format[Points] = Json.format

    Json.format
  }

}

abstract class StringSampleSolutionToolJsonProtocol[C <: ExerciseContent, PartType <: ExPart]
    extends ToolJsonProtocol[String, C, PartType] {

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

}
