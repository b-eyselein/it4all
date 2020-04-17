package model.tools

import model.json.KeyValueObject
import play.api.libs.json._

final case class ReadExercisesMessage[S, C, E <: Exercise[S, C]](exercises: Seq[E])

trait ToolJsonProtocol[S, C, E <: Exercise[S, C], PartType <: ExPart] {

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

  val exerciseFormat: Format[E]

  val partTypeFormat: Format[PartType]

  lazy val readExercisesMessageReads: Reads[ReadExercisesMessage[S, C, E]] = {
    implicit val ef: Format[E] = exerciseFormat

    Json.reads
  }

  def validateAndWriteReadExerciseMessage(message: JsValue): Seq[String] = {
    val readExercises: Seq[E] = readExercisesMessageReads.reads(message) match {
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

abstract class StringSampleSolutionToolJsonProtocol[C, E <: Exercise[String, C], PartType <: ExPart]
    extends ToolJsonProtocol[String, C, E, PartType] {

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

}
