package model.sql

import model.core.matching.{GenericAnalysisResult, MatchingResult}
import model.sql.matcher._
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.select.OrderByElement

import scala.util.{Failure, Success, Try}

abstract class QueryCorrector(val queryType: String) {

  protected type Q <: net.sf.jsqlparser.statement.Statement

  def correct(solutionSaved: Boolean, database: SqlExecutionDAO, learnerSolution: String, sampleSolution: SqlSample, exercise: SqlCompleteEx, scenario: SqlScenario): SqlCorrResult = {
    val statementParseTries = for {
      userStatement <- parseStatement(learnerSolution) flatMap checkStatement
      sampleStatement <- parseStatement(sampleSolution.sample) flatMap checkStatement
    } yield (userStatement, sampleStatement)

    statementParseTries match {
      case Success((userQ, sampleQ)) => correctQueries(solutionSaved, learnerSolution, database, exercise, scenario, userQ, sampleQ)
      case Failure(error)            => SqlParseFailed(solutionSaved, learnerSolution, error)
    }
  }

  private def correctQueries(solutionSaved: Boolean, learnerSolution: String, database: SqlExecutionDAO, exercise: SqlCompleteEx, scenario: SqlScenario, userQ: Q, sampleQ: Q) = {
    val (userTAliases, sampleTAliases) = (resolveAliases(userQ), resolveAliases(sampleQ))

    val tableComparison = compareTables(userQ, sampleQ)

    val columnComparison = compareColumns(userQ, userTAliases, sampleQ, sampleTAliases)

    val whereComparison = compareWhereClauses(userQ, userTAliases, sampleQ, sampleTAliases)

    val executionResult: SqlExecutionResult = database.executeQueries(scenario, exercise, userQ, sampleQ)

    val groupByComparison = compareGroupByElements(userQ, sampleQ)
    val orderByComparison = compareOrderByElements(userQ, sampleQ)

    SqlResult(solutionSaved, learnerSolution, columnComparison, tableComparison, whereComparison, executionResult, groupByComparison, orderByComparison)
  }

  def compareColumns(userQ: Q, userTAliases: Map[String, String], sampleQ: Q, sampleTAliases: Map[String, String]): MatchingResult[ColumnWrapper, GenericAnalysisResult, ColumnMatch] =
    ColumnMatcher.doMatch(getColumnWrappers(userQ), getColumnWrappers(sampleQ))

  def compareWhereClauses(userQ: Q, userTAliases: Map[String, String], sampleQ: Q, sampleTAliases: Map[String, String]): MatchingResult[BinaryExpression, GenericAnalysisResult, BinaryExpressionMatch] =
    new BinaryExpressionMatcher(userTAliases, sampleTAliases).doMatch(getExpressions(userQ), getExpressions(sampleQ))

  def getExpressions(statement: Q): Seq[BinaryExpression] = getWhere(statement) match {
    case None             => Seq.empty
    case Some(expression) => new ExpressionExtractor(expression).extracted
  }

  def compareTables(userQ: Q, sampleQ: Q): MatchingResult[Table, GenericAnalysisResult, TableMatch] = TableMatcher.doMatch(getTables(userQ), getTables(sampleQ))

  def resolveAliases(query: Q): Map[String, String] = getTables(query).filter(q => Option(q.getAlias).isDefined).map(t => t.getAlias.getName -> t.getName).toMap


  protected def getColumnWrappers(query: Q): Seq[ColumnWrapper]

  protected def getTables(query: Q): Seq[Table]

  protected def getWhere(query: Q): Option[Expression]

  protected def compareGroupByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[MatchingResult[Expression, GenericAnalysisResult, GroupByMatch]] = None

  protected def compareOrderByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[MatchingResult[OrderByElement, GenericAnalysisResult, OrderByMatch]] = None

  // Parsing

  private def parseStatement(str: String): Try[Statement] = Try(CCJSqlParserUtil.parse(str)) match {
    case Failure(e) => Failure(new SqlStatementException(e))
    case other      => other
  }

  protected def checkStatement(statement: Statement): Try[Q] // FIXME: Failure!?!

}
