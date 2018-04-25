package model.core.result

import enumeratum.{EnumEntry, Enum, PlayJsonEnum}

import scala.collection.immutable.IndexedSeq


sealed abstract class SuccessType(val points: Int, val color: String, val glyphicon: String) extends EnumEntry


object SuccessType extends Enum[SuccessType] with PlayJsonEnum[SuccessType] {

  val values: IndexedSeq[SuccessType] = findValues

  case object ERROR extends SuccessType(0, "danger", "glyphicon glyphicon-remove")

  case object NONE extends SuccessType(0, "danger", "glyphicon glyphicon-remove")

  case object PARTIALLY extends SuccessType(1, "warning", "glyphicon glyphicon-question-sign")

  case object COMPLETE extends SuccessType(2, "success", "glyphicon glyphicon-ok")

  def ofBool(success: Boolean): SuccessType = if (success) COMPLETE else NONE

}
