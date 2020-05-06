package model.tools.sql

import enumeratum.{EnumEntry, PlayEnum}
import model._

sealed abstract class SqlExPart(val partName: String, val id: String) extends ExPart

object SqlExPart extends ExParts[SqlExPart] {

  val values: IndexedSeq[SqlExPart] = findValues

  case object SqlSingleExPart extends SqlExPart(partName = "Bearbeiten", id = "solve")

}

object SqlTopics {
  val Join: Topic      = Topic("J", "sql", "Join")
  val OrderBy: Topic   = Topic("O", "sql", "Reihenfolge")
  val GroupBy: Topic   = Topic("G", "sql", "Gruppierung")
  val Aggregate: Topic = Topic("F", "sql", "Aggregatsfunktion")
  val Alias: Topic     = Topic("A", "sql", "Alias")
  val Limit: Topic     = Topic("L", "sql", "Limitierung")
  val SubSelect: Topic = Topic("S", "sql", "Zweites Select innerhalb")

  val Create: Topic = Topic("C", "sql", "Create")
  val Update: Topic = Topic("U", "sql", "Update")
  val Insert: Topic = Topic("I", "sql", "Insert")
  val Delete: Topic = Topic("D", "sql", "Delete")

  val values: Seq[Topic] = Seq(
    Join,
    OrderBy,
    GroupBy,
    Aggregate,
    Alias,
    Limit,
    SubSelect
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
