package model.tools.sql

import enumeratum.{EnumEntry, PlayEnum}
import model.{ExPart, ExTag}

import scala.collection.immutable
import scala.collection.immutable.IndexedSeq


sealed abstract class SqlExPart(val partName: String, val urlName: String) extends EnumEntry with ExPart

object SqlExParts extends PlayEnum[SqlExPart] {

  val values: immutable.IndexedSeq[SqlExPart] = findValues

  case object SqlSingleExPart extends SqlExPart("Bearbeiten", "solve")

}


sealed abstract class SqlExTag(val buttonContent: String, val title: String) extends ExTag with EnumEntry


object SqlExTag extends PlayEnum[SqlExTag] {

  override def values: IndexedSeq[SqlExTag] = findValues

  // Types of queries?

  case object SQL_SELECT extends SqlExTag("S", "SELECT")

  case object SQL_UPDATE extends SqlExTag("U", "UPDATE")

  case object SQL_INSERT extends SqlExTag("I", "INSERT")

  case object SQL_DELETE extends SqlExTag("D", "DELETE")

  // Constructs used in queryy

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
