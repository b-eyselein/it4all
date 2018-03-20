package model.core

import model.Enums.ExerciseState
import play.api.data.FormError
import play.api.data.format.Formatter

trait ExerciseFormMappings {

  protected implicit object ExerciseStateFormatter extends Formatter[ExerciseState] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], ExerciseState] = data.get(key) match {
      case None           => Left(Seq(FormError(key, "No value found!")))
      case Some(valueStr) => ExerciseState.byString(valueStr) match {
        case Some(state) => Right(state)
        case None        => Left(Seq(FormError(key, s"Value '$valueStr' is no legal value!")))
      }
    }

    override def unbind(key: String, value: ExerciseState): Map[String, String] = Map(key -> value.name)

  }

}
