package model.sql

import model.CollectionConsts

object SqlConsts extends CollectionConsts {

  val COLUMNS_NAME    = "Spalten"
  val CONDITIONS_NAME = "Bedingungen"

  val Delimiter = ";"

  val hintName = "hint"

  val NewLine = "\n"

  val sampleName  = "sample"
  val samplesName = "samples"

  def samplesArrayName(count: Int = -1): String = arrayName(samplesName)(if (count > 0) Some(count) else None)

  val shortNameName    = "shortName"
  val SELECT_ALL_DUMMY = "SELECT * FROM "
  val SHOW_ALL_TABLES  = "SHOW TABLES"

  val TablesName            = "Tabellen"
  val tagsName              = "tags"
  val tagsArrayName: String = arrayName(tagsName)(None)

}
