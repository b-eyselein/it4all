package model.sql

import enumeratum.{Enum, EnumEntry}
import model.ExTag

import scala.collection.immutable.IndexedSeq

sealed abstract class SqlExTag(buttonContent: String, title: String) extends ExTag with EnumEntry


object SqlExTag extends Enum[SqlExTag] {

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
