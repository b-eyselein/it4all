package model.tools.sql

import enumeratum.{EnumEntry, PlayEnum}
import model.{ExPart, ExParts, ExerciseContent, SampleSolution, Topic}
import model.tools._

sealed abstract class SqlExPart(val partName: String, val id: String) extends ExPart

object SqlExPart extends ExParts[SqlExPart] {

  val values: IndexedSeq[SqlExPart] = findValues

  case object SqlSingleExPart extends SqlExPart(partName = "Bearbeiten", id = "solve")

}

object SqlTopics {
  val values: Seq[Topic] = Seq(
    Topic("J", "sql", "Join"),
    Topic("2J", "sql", "Zweifacher Join"),
    Topic("3J", "sql", "Dreifacher Join"),
    Topic("O", "sql", "Reihenfolge"),
    Topic("G", "sql", "Gruppierung"),
    Topic("F", "sql", "Funktion"),
    Topic("A", "sql", "Alias"),
    Topic("L", "sql", "Limitierung"),
    Topic("S", "sql", "Zweites Select innerhalb")
  )
}

sealed trait SqlExerciseType extends EnumEntry

object SqlExerciseType extends PlayEnum[SqlExerciseType] {

  override def values: IndexedSeq[SqlExerciseType] = findValues

  case object SELECT extends SqlExerciseType

  case object CREATE extends SqlExerciseType

  case object UPDATE extends SqlExerciseType

  case object INSERT extends SqlExerciseType

  case object DELETE extends SqlExerciseType

}

final case class SqlExerciseContent(
  exerciseType: SqlExerciseType,
  schemaName: String,
  sampleSolutions: Seq[SampleSolution[String]],
  hint: Option[String] = None
) extends ExerciseContent[String] {

  override def parts: Seq[ExPart] = SqlExPart.values

}
