package model

import enumeratum.{EnumEntry, PlayEnum}

// Exercise State

sealed abstract class ExerciseState(val german: String) extends EnumEntry


object ExerciseState extends PlayEnum[ExerciseState] {

  override val values: IndexedSeq[ExerciseState] = findValues

  case object RESERVED extends ExerciseState("Reserviert")

  case object CREATED extends ExerciseState("Erstellt")

  case object ACCEPTED extends ExerciseState("Akzeptiert")

  case object APPROVED extends ExerciseState("Zugelassen")

}

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
    val marks = difficulties.filter(_ != Difficulties.NOT_SPECIFIED).map(_.value)

    // Round to max two decimal places
    math.rint(marks.sum.toDouble / marks.length * 100) / 100
  }

}

