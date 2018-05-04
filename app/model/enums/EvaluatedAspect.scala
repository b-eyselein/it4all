package model.enums

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

sealed abstract class EvaluatedAspect(val question: String, val positive: String, val neutral: String, val negative: String) extends EnumEntry

object EvaluatedAspects extends Enum[EvaluatedAspect] {

  override def values: immutable.IndexedSeq[EvaluatedAspect] = findValues


  case object USED extends EvaluatedAspect("Wie oft haben Sie dieses Tool genutzt?", "Oft", "Manchmal", "Selten")

  case object SENSE extends EvaluatedAspect("Finden Sie dieses Tool sinnvoll?", "Sinnvoll", "Neutral", "Sinnlos")

  case object USABILITY extends EvaluatedAspect("Wie bewerten Sie die allgemeine Bedienbarkeit dieses Tools?", "Gut", "Neutral", "Schlecht")

  case object STYLE_OF_FEEDBACK extends EvaluatedAspect("Wie bewerten Sie die Gestaltung des Feedbacks dieses Tools?", "Gut", "Neutral", "Schlecht")

  case object FAIRNESS_OF_FEEDBACK extends EvaluatedAspect("Wie bewerten Sie die Fairness der Evaluation dieses Tools?", "Fair", "Neutral", "Unfair");

}
