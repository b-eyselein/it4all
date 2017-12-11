package model.sql

import model.core.CommonUtils.RicherTry
import model.core.matching._
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.Table

import scala.util.{Failure, Success, Try}

case class TableMatch(userArg: Option[Table], sampleArg: Option[Table]) extends Match[Table] {

  override val size: Int = TableMatcher.headings.size

  override def descArg(arg: Table): String = arg.getName

}

object TableMatcher extends Matcher[Table, TableMatch, TableMatchingResult](Seq("Tabellenname"), _.getName == _.getName, TableMatch, TableMatchingResult)

case class TableMatchingResult(allMatches: Seq[TableMatch]) extends MatchingResult[Table, TableMatch] {

  override val matchName: String      = "Tabellen"
  override val headings : Seq[String] = TableMatcher.headings

}


abstract class QueryCorrector(val queryType: String) {

  protected type Q <: net.sf.jsqlparser.statement.Statement

  type AliasMap = Map[String, String]

  def correct(database: SqlExecutionDAO, learnerSolution: String, sampleStatement: SqlSample, exercise: SqlCompleteEx, scenario: SqlScenario): SqlCorrResult =
    parseStatement(learnerSolution) zip parseStatement(sampleStatement.sample) match {
      case Success((userQ, sampleQ)) => correctQueries(learnerSolution, database, scenario, userQ, sampleQ)
      case Failure(_)                => SqlFailed(learnerSolution)
    }

  private def correctQueries(learnerSolution: String, database: SqlExecutionDAO, scenario: SqlScenario, userQ: Q, sampleQ: Q) = {
    val (userTAliases, sampleTAliases) = (resolveAliases(userQ), resolveAliases(sampleQ))

    val tableComparison = compareTables(userQ, sampleQ)

    val columnComparison = compareColumns(userQ, userTAliases, sampleQ, sampleTAliases)

    val whereComparison = compareWhereClauses(userQ, userTAliases, sampleQ, sampleTAliases)

    val executionResult = database.executeQueries(scenario.shortName, userQ.toString, sampleQ.toString)

    val groupByComparison = compareGroupByElements(userQ, sampleQ)
    val orderByComparison = compareOrderByElements(userQ, sampleQ)

    SqlResult(learnerSolution, columnComparison, tableComparison, whereComparison, executionResult, groupByComparison, orderByComparison)
  }

  def compareColumns(userQ: Q, userTAliases: AliasMap, sampleQ: Q, sampleTAliases: AliasMap): ColumnMatchingResult =
    ColumnMatcher.doMatch(getColumnWrappers(userQ), getColumnWrappers(sampleQ))

  def compareWhereClauses(userQ: Q, userTAliases: AliasMap, sampleQ: Q, sampleTAliases: AliasMap): BinaryExpressionMatchingResult =
    new BinaryExpressionMatcher(userTAliases, sampleTAliases).doMatch(getExpressions(userQ), getExpressions(sampleQ))

  def getExpressions(statement: Q): Seq[BinaryExpression] = getWhere(statement) match {
    case None             => Seq.empty
    case Some(expression) => new ExpressionExtractor(expression).extracted
  }

  def compareTables(userQ: Q, sampleQ: Q): TableMatchingResult = TableMatcher.doMatch(getTables(userQ), getTables(sampleQ))

  def resolveAliases(query: Q): Map[String, String] = getTables(query).filter(_.getAlias != null).map(t => t.getAlias.getName -> t.getName).toMap

  // FIXME: Failure!
  protected def parseStatement(statement: String): Try[Q]

  protected def getColumnWrappers(query: Q): Seq[ColumnWrapper]

  protected def getTables(query: Q): Seq[Table]

  protected def getWhere(query: Q): Option[Expression]

  protected def compareGroupByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[GroupByMatchingResult] = None

  protected def compareOrderByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[OrderByMatchingResult] = None

}
