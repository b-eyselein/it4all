package model.sql

import enumeratum.{EnumEntry, PlayEnum}
import model.ExPart

import scala.collection.immutable

sealed abstract class SqlExPart(val partName: String, val urlName: String) extends EnumEntry with ExPart

object SqlExParts extends PlayEnum[SqlExPart] {

  val values: immutable.IndexedSeq[SqlExPart] = findValues

  object SqlSingleExPart extends SqlExPart("Bearbeiten", "solve")

}