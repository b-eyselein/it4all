package model.core.overviewHelpers

import enumeratum.{EnumEntry, PlayEnum}
import model.{ExPart, Exercise}

import scala.collection.immutable.IndexedSeq

sealed abstract class SolvedState(val btnOutline: String) extends EnumEntry

object SolvedStates extends PlayEnum[SolvedState] {

  override def values: IndexedSeq[SolvedState] = findValues

  case object NotStarted extends SolvedState("btn-outline-secondary")

  case object PartlySolved extends SolvedState("btn-outline-warning")

  case object CompletelySolved extends SolvedState("btn-outline-success")

}


final case class SolvedStatesForExerciseParts[PartType <: ExPart](ex: Exercise, solvedStates: Map[PartType, SolvedState])
