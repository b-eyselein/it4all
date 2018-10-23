package model

import enumeratum.{EnumEntry, PlayEnum}

import scala.collection.immutable
import scala.collection.immutable.IndexedSeq


// Difficulty

sealed abstract class Difficulty(val value: Int, val german: String) extends EnumEntry

case object Difficulties extends PlayEnum[Difficulty] {

  override val values: IndexedSeq[Difficulty] = findValues


  case object NOT_SPECIFIED extends Difficulty(0, "Keine Angabe")


  case object VERY_EASY extends Difficulty(1, "Sehr leicht")

  case object EASY extends Difficulty(2, "Leicht")

  case object MEDIUM extends Difficulty(3, "Mittel")

  case object HARD extends Difficulty(4, "Schwer")

  case object VERY_HARD extends Difficulty(5, "Sehr schwer")


  def avg(difficulties: Seq[Difficulty]): Double = {
    val marks = difficulties filter (_ != Difficulties.NOT_SPECIFIED) map (_.value)

    // Round to max two decimal places
    math.rint(marks.sum.toDouble / marks.length * 100) / 100
  }

}

// Mark

sealed abstract class Mark(val value: Int) extends EnumEntry {

  def display(evaluatedAspect: EvaluatedAspect): String

}

object Mark extends PlayEnum[Mark] {

  override val values: IndexedSeq[Mark] = findValues

  case object VeryGood extends Mark(1) {
    override def display(evaluatedAspect: EvaluatedAspect): String = "Sehr " + evaluatedAspect.positive.toLowerCase
  }

  case object Good extends Mark(2) {
    override def display(evaluatedAspect: EvaluatedAspect): String = evaluatedAspect.positive
  }

  case object Neutral extends Mark(3) {
    override def display(evaluatedAspect: EvaluatedAspect): String = evaluatedAspect.neutral
  }

  case object Bad extends Mark(4) {
    override def display(evaluatedAspect: EvaluatedAspect): String = evaluatedAspect.negative
  }

  case object VeryBad extends Mark(5) {
    override def display(evaluatedAspect: EvaluatedAspect): String = "Sehr " + evaluatedAspect.negative.toLowerCase
  }

  case object NoMark extends Mark(-1) {
    override def display(evaluatedAspect: EvaluatedAspect): String = "Keine Angabe"
  }

}

// Evaluated Aspect

sealed abstract class EvaluatedAspect(val question: String, val positive: String, val neutral: String, val negative: String) extends EnumEntry

object EvaluatedAspects extends PlayEnum[EvaluatedAspect] {

  override def values: immutable.IndexedSeq[EvaluatedAspect] = findValues


  case object USED extends EvaluatedAspect("Wie oft haben Sie dieses Tool genutzt?", "Oft", "Manchmal", "Selten")

  case object SENSE extends EvaluatedAspect("Finden Sie dieses Tool sinnvoll?", "Sinnvoll", "Neutral", "Sinnlos")

  case object USABILITY extends EvaluatedAspect("Wie bewerten Sie die allgemeine Bedienbarkeit dieses Tools?", "Gut", "Neutral", "Schlecht")

  case object STYLE_OF_FEEDBACK extends EvaluatedAspect("Wie bewerten Sie die Gestaltung des Feedbacks dieses Tools?", "Gut", "Neutral", "Schlecht")

  case object FAIRNESS_OF_FEEDBACK extends EvaluatedAspect("Wie bewerten Sie die Fairness der Evaluation dieses Tools?", "Fair", "Neutral", "Unfair");

}
