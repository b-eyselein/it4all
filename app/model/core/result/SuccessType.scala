package model.core.result

import enumeratum.{EnumEntry, PlayEnum}

import scala.collection.immutable.IndexedSeq


sealed abstract class SuccessType(val color: String, val glyphicon: String) extends EnumEntry


object SuccessType extends PlayEnum[SuccessType] {

  override val values: IndexedSeq[SuccessType] = findValues

  case object ERROR extends SuccessType("danger", "glyphicon glyphicon-remove")

  case object NONE extends SuccessType("danger", "glyphicon glyphicon-remove")

  case object PARTIALLY extends SuccessType("warning", "glyphicon glyphicon-question-sign")

  case object COMPLETE extends SuccessType("success", "glyphicon glyphicon-ok")

  def ofBool(success: Boolean): SuccessType = if (success) COMPLETE else NONE

}
