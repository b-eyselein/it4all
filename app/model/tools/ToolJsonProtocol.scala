package model.tools

import model.json.{JsonProtocols, KeyValueObject}
import play.api.libs.json._

final case class ReadExercisesMessage[S, C](
  exercises: Seq[Exercise[C, S]]
)

trait ToolJsonProtocol[S, C, PartType <: ExPart] {

  protected val keyValueObjectMapFormat: Format[Map[String, String]] = {

    val keyValueObjectFormat: Format[KeyValueObject] = Json.format[KeyValueObject]

    Format(
      Reads.seq(keyValueObjectFormat).map(_.map { case KeyValueObject(key, value) => (key, value) }.toMap),
      Writes.seq(keyValueObjectFormat).contramap(_.toSeq.map { case (key, value) => KeyValueObject(key, value) })
    )
  }

  val solutionFormat: Format[S]

  lazy val sampleSolutionFormat: Format[SampleSolution[S]] = {
    implicit val sf: Format[S] = solutionFormat

    Json.format
  }

  val exerciseContentFormat: Format[C]

  final lazy val exerciseFormat: Format[Exercise[C, S]] = {
    implicit val tf: Format[Topic]              = JsonProtocols.topicFormat
    implicit val fc: Format[C]                  = exerciseContentFormat
    implicit val ssf: Format[SampleSolution[S]] = sampleSolutionFormat

    Json.format
  }

  val partTypeFormat: Format[PartType]

  /*
  lazy val readExerciseFormat: Format[ReadExercise[S, C]] = {

    implicit val sf: Format[SampleSolution[S]] = sampleSolutionFormat
    implicit val cf: Format[C]                 = exerciseContentFormat

    Json.format
  }
   */

  lazy val readExercisesMessageReads: Reads[ReadExercisesMessage[S, C]] = {

    implicit val ef: Format[Exercise[C, S]] = exerciseFormat

    Json.reads
  }

  def validateAndWriteReadExerciseMessage(message: JsValue): Seq[String] = {
    val readExercises: Seq[Exercise[C, S]] = readExercisesMessageReads.reads(message) match {
      case JsSuccess(readExercisesMessage, _) => readExercisesMessage.exercises
      case JsError(errors) =>
        errors.foreach(println)
        Seq.empty
    }

    readExercises
      .map(exerciseFormat.writes)
      .map(Json.stringify)
  }

}

abstract class StringSampleSolutionToolJsonProtocol[C, PartType <: ExPart]
    extends ToolJsonProtocol[String, C, PartType] {

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

}
