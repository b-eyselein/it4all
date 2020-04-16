package model.tools

import model.json.KeyValueObject
import play.api.libs.json._

final case class ReadExercisesMessage[E <: Exercise](exercises: Seq[E])

trait ToolJsonProtocol[EC <: Exercise, ST, PartType <: ExPart] {

  protected val keyValueObjectMapFormat: Format[Map[String, String]] = {

    val keyValueObjectFormat: Format[KeyValueObject] = Json.format[KeyValueObject]

    Format(
      Reads.seq(keyValueObjectFormat).map(_.map { case KeyValueObject(key, value) => (key, value) }.toMap),
      Writes.seq(keyValueObjectFormat).contramap(_.toSeq.map { case (key, value) => KeyValueObject(key, value) })
    )
  }

  val exerciseFormat: Format[EC]

  val solutionFormat: Format[ST]

  val partTypeFormat: Format[PartType]

  val readExercisesMessageReads: Reads[ReadExercisesMessage[EC]]

  def validateAndWriteReadExerciseMessage(message: JsValue): Seq[String] = {
    val readExercises: Seq[EC] = readExercisesMessageReads.reads(message) match {
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

abstract class StringSampleSolutionToolJsonProtocol[E <: Exercise, PartType <: ExPart]
    extends ToolJsonProtocol[E, String, PartType] {

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

  protected val sampleSolutionFormat: Format[SampleSolution[String]] = Json.format[SampleSolution[String]]

}
