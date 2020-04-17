package model.tools.sql

import enumeratum.{EnumEntry, PlayEnum}
import model.tools._

sealed abstract class SqlExPart(val partName: String, val urlName: String) extends ExPart

object SqlExParts extends ExParts[SqlExPart] {

  val values: IndexedSeq[SqlExPart] = findValues

  case object SqlSingleExPart extends SqlExPart("Bearbeiten", "solve")

}

sealed abstract class SqlExTag(val buttonContent: String, val title: String) extends EnumEntry

object SqlExTag extends PlayEnum[SqlExTag] {

  override def values: IndexedSeq[SqlExTag] = findValues

  case object SQL_JOIN extends SqlExTag("J", "Join")

  case object SQL_DOUBLE_JOIN extends SqlExTag("2J", "Zweifacher Join")

  case object SQL_TRIPLE_JOIN extends SqlExTag("3J", "Dreifacher Join")

  case object SQL_ORDER_BY extends SqlExTag("O", "Reihenfolge")

  case object SQL_GROUP_BY extends SqlExTag("G", "Gruppierung")

  case object SQL_FUNCTION extends SqlExTag("F", "Funktion")

  case object SQL_ALIAS extends SqlExTag("A", "Alias")

  case object SQL_LIMIT extends SqlExTag("L", "Limitierung")

  case object SQL_SUBSELECT extends SqlExTag("S", "Zweites Select innerhalb")

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

final case class SqlExercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  topics: Seq[Topic],
  difficulty: Int,
  sampleSolutions: Seq[SampleSolution[String]],
  content: SqlExerciseContent
) extends Exercise[String, SqlExerciseContent]

final case class SqlExerciseContent(
  exerciseType: SqlExerciseType,
  hint: Option[String]
)
