package model.core

import com.google.common.base.Splitter
import model.Enums.ExerciseState
import play.api.libs.json._

import scala.language.implicitConversions

abstract class BaseFormats[B <: HasBaseValues] {

  protected val FixedSplitter: Splitter = Splitter.fixedLength(100)

  implicit val reads: Reads[B]

  implicit val writes: Writes[B]


  implicit def exStateReads: Reads[ExerciseState] = {
    case JsString(str) => JsSuccess(Option(ExerciseState.valueOf(str)).getOrElse(ExerciseState.ACCEPTED))
    case _             => JsError("!")
  }

  implicit protected def exStateWrites: Writes[ExerciseState] = (es: ExerciseState) => JsString(es.name)

  implicit protected def exStateFormat(es: ExerciseState): Format[ExerciseState] = Format(exStateReads, exStateWrites)

}
