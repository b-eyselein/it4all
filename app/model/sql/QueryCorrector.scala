package model.sql

import model.core.CommonUtils.RicherTry
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement

import scala.util.{Failure, Success, Try}

abstract class QueryCorrector(val queryType: String) {

  protected type Q <: net.sf.jsqlparser.statement.Statement

  type AliasMap = Map[String, String]

  def correct(database: SqlExecutionDAO, learnerSolution: String, sampleSolution: SqlSample, exercise: SqlCompleteEx, scenario: SqlScenario): SqlCorrResult = {
    val userStatement: Try[Q] = parseStatement(learnerSolution) flatMap checkStatement
    val sampleStatement: Try[Q] = parseStatement(sampleSolution.sample) flatMap checkStatement

    userStatement zip sampleStatement match {
      case Success((userQ, sampleQ)) => correctQueries(learnerSolution, database, exercise, scenario, userQ, sampleQ)
      case Failure(error)            => SqlParseFailed(learnerSolution, error)
    }
  }

  private def correctQueries(learnerSolution: String, database: SqlExecutionDAO, exercise: SqlCompleteEx, scenario: SqlScenario, userQ: Q, sampleQ: Q) = {
    val (userTAliases, sampleTAliases) = (resolveAliases(userQ), resolveAliases(sampleQ))

    val tableComparison = compareTables(userQ, sampleQ)

    val columnComparison = compareColumns(userQ, userTAliases, sampleQ, sampleTAliases)

    val whereComparison = compareWhereClauses(userQ, userTAliases, sampleQ, sampleTAliases)

    val executionResult = database.executeQueries(scenario, exercise, userQ, sampleQ)

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

  def resolveAliases(query: Q): Map[String, String] = getTables(query).filter(q => Option(q.getAlias).isDefined).map(t => t.getAlias.getName -> t.getName).toMap


  protected def getColumnWrappers(query: Q): Seq[ColumnWrapper]

  protected def getTables(query: Q): Seq[Table]

  protected def getWhere(query: Q): Option[Expression]

  protected def compareGroupByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[GroupByMatchingResult] = None

  protected def compareOrderByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[OrderByMatchingResult] = None

  // Parsing

  private def parseStatement(str: String): Try[Statement] = Try(CCJSqlParserUtil.parse(str)) match {
    case Failure(e) => Failure(new SqlStatementException(e))
    case other      => other
  }

  protected def checkStatement(statement: Statement): Try[Q] // FIXME: Failure!?!

}
