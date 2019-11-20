package model.tools.collectionTools.sql

import enumeratum.{EnumEntry, PlayEnum}
import model.tools.collectionTools.{ExPart, ExParts, ExTag}


sealed abstract class SqlExPart(val partName: String, val urlName: String) extends ExPart

object SqlExParts extends ExParts[SqlExPart] {

  val values: IndexedSeq[SqlExPart] = findValues


  case object SqlSingleExPart extends SqlExPart("Bearbeiten", "solve")

}


sealed abstract class SqlExerciseTag(val buttonContent: String, val title: String) extends ExTag

object SqlExerciseTag extends PlayEnum[SqlExerciseTag] {

  override def values: IndexedSeq[SqlExerciseTag] = findValues


  case object SQL_JOIN extends SqlExerciseTag("J", "Join")

  case object SQL_DOUBLE_JOIN extends SqlExerciseTag("2J", "Zweifacher Join")

  case object SQL_TRIPLE_JOIN extends SqlExerciseTag("3J", "Dreifacher Join")

  case object SQL_ORDER_BY extends SqlExerciseTag("O", "Reihenfolge")

  case object SQL_GROUP_BY extends SqlExerciseTag("G", "Gruppierung")

  case object SQL_FUNCTION extends SqlExerciseTag("F", "Funktion")

  case object SQL_ALIAS extends SqlExerciseTag("A", "Alias")

  case object SQL_LIMIT extends SqlExerciseTag("L", "Limitierung")

  case object SQL_SUBSELECT extends SqlExerciseTag("S", "Zweites Select innerhalb")

}
