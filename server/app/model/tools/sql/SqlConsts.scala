package model.tools.sql

import model.CollectionConsts

object SqlConsts extends CollectionConsts {

  val additionalComparisonsName: String = "additionalComparisons"

  val columnComparisonsName: String = "columnComparisons"

  val sqlDelimiter: String = ";"

  val executionResultsName: String = "executionResults"

  val hintName: String = "hint"

  val joinExpressionsComparisonsName: String = "joinExpressionComparisons"

  val sampleResultName: String = "sampleResult"
  val SELECT_ALL_DUMMY         = "SELECT * FROM "

  val tableComparisonsName: String = "tableComparisons"

  val userResultName: String = "userResult"

  val whereComparisonsName: String = "whereComparisons"

  def samplesArrayName(count: Int = -1): String = arrayName(samplesName)(if (count > 0) Some(count) else None)

}
