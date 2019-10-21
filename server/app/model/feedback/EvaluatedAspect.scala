package model.feedback

import enumeratum.{EnumEntry, PlayEnum}

sealed abstract class EvaluatedAspect(
  val question: String,
  val positive: String = "Gut",
  val neutral: String = "Neutral",
  val negative: String = "Schlecht"
) extends EnumEntry


object EvaluatedAspects extends PlayEnum[EvaluatedAspect] {

  override def values: IndexedSeq[EvaluatedAspect] = findValues


  case object USED extends EvaluatedAspect("Wie oft haben Sie dieses Tool genutzt?", positive = "Oft", "Manchmal", negative = "Selten")

  case object SENSE extends EvaluatedAspect("Finden Sie dieses Tool sinnvoll?", positive = "Sinnvoll", negative = "Sinnlos")

  case object USABILITY extends EvaluatedAspect("Wie bewerten Sie die allgemeine Bedienbarkeit dieses Tools?")

  case object STYLE_OF_FEEDBACK extends EvaluatedAspect("Wie bewerten Sie die Gestaltung des Feedbacks dieses Tools?")

  case object FAIRNESS_OF_FEEDBACK extends EvaluatedAspect("Wie bewerten Sie die Fairness der Evaluation dieses Tools?", positive = "Fair", negative = "Unfair")

}
