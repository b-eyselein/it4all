package model.sql

import model.CollectionConsts

object SqlConsts extends CollectionConsts {

  val additionalComparisonsName = "additionalComparisons"

  val columnComparisonsName = "columnComparisons"
  val COLUMNS_NAME          = "Spalten"

  val sqlDelimiter = ";"

  val executionName = "executionResults"

  val hintName = "hint"

  val joinExpressionsComparisonsName = "joinExpressionComparisons"

  val sampleResultName = "sampleResult"
  val shortNameName    = "shortName"
  val SELECT_ALL_DUMMY = "SELECT * FROM "
  val SHOW_ALL_TABLES  = "SHOW TABLES"

  val tableComparisonsName  = "tableComparisons"
  val tagsName              = "tags"
  val tagsArrayName: String = arrayName(tagsName)(None)

  val userErrorName  = "userError"
  val userResultName = "userResult"

  val whereComparisonsName = "whereComparisons"

  def samplesArrayName(count: Int = -1): String = arrayName(samplesName)(if (count > 0) Some(count) else None)

}
