package model.tools.collectionTools.sql

import model.tools.{ToolConsts, ToolState}

object SqlConsts extends ToolConsts {

  override val toolName : String    = "Sql"
  override val toolId   : String    = "sql"
  override val toolState: ToolState = ToolState.LIVE

  val additionalComparisonsName: String = "additionalComparisons"

  val columnComparisonsName: String = "columnComparisons"

  val sqlDelimiter: String = ";"

  val executionResultsName: String = "executionResults"

  val joinExpressionsComparisonsName: String = "joinExpressionComparisons"

  val sampleResultName: String = "sampleResult"
  val SELECT_ALL_DUMMY         = "SELECT * FROM "

  val tableComparisonsName: String = "tableComparisons"

  val userResultName: String = "userResult"

  val whereComparisonsName: String = "whereComparisons"

}
