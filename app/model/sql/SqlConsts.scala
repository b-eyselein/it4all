package model.sql

import model.CollectionConsts

object SqlConsts extends CollectionConsts {

  val columnsName     = "columns"
  val COLUMNS_NAME    = "Spalten"
  val CONDITIONS_NAME = "Bedingungen"

  val Delimiter = ";"

  val executionName = "executionResults"

  val hintName = "hint"

  val NewLine = "\n"

  val sampleName       = "sample"
  val samplesName      = "samples"
  val sampleResultName = "sampleResult"
  val shortNameName    = "shortName"
  val SELECT_ALL_DUMMY = "SELECT * FROM "
  val SHOW_ALL_TABLES  = "SHOW TABLES"

  val tablesName            = "tables"
  val tagsName              = "tags"
  val tagsArrayName: String = arrayName(tagsName)(None)

  val userErrorName  = "userError"
  val userResultName = "userResult"


  def samplesArrayName(count: Int = -1): String = arrayName(samplesName)(if (count > 0) Some(count) else None)

}
