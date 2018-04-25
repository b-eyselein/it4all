package model.enums

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable.IndexedSeq

sealed abstract class Mark(val value: Int) extends EnumEntry {

  def display(evaluatedAspect: EvaluatedAspect): String

  def isChecked(that: Mark): String = if (this == that) "checked" else ""

}

object Marks extends Enum[Mark] {

  val values: IndexedSeq[Mark] = findValues

  case object VERY_GOOD extends Mark(1) {
    override def display(evaluatedAspect: EvaluatedAspect): String = "Sehr " + evaluatedAspect.positive.toLowerCase
  }

  case object GOOD extends Mark(2) {
    override def display(evaluatedAspect: EvaluatedAspect): String = evaluatedAspect.positive
  }

  case object NEUTRAL extends Mark(3) {
    override def display(evaluatedAspect: EvaluatedAspect): String = evaluatedAspect.neutral
  }

  case object BAD extends Mark(4) {
    override def display(evaluatedAspect: EvaluatedAspect): String = evaluatedAspect.negative
  }

  case object VERY_BAD extends Mark(5) {
    override def display(evaluatedAspect: EvaluatedAspect): String = "Sehr " + evaluatedAspect.negative.toLowerCase
  }

  case object NO_MARK extends Mark(-1) {
    override def display(evaluatedAspect: EvaluatedAspect): String = "Keine Angabe"
  }

}
