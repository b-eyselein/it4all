package model.sql

import model._
import model.core.matching.{Match, MatchingResult}
import model.sql.matcher._
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

abstract class QueryCorrector(val queryType: String) {

  protected type Q <: net.sf.jsqlparser.statement.Statement

  def correct(database: SqlExecutionDAO, learnerSolution: String, sampleSolution: SqlSample, exercise: SqlCompleteEx, scenario: SqlScenario): SqlCorrResult = {
    val statementParseTries = for {
      userStatement <- parseStatement(learnerSolution) flatMap checkStatement
      sampleStatement <- parseStatement(sampleSolution.sample) flatMap checkStatement
    } yield (userStatement, sampleStatement)

    statementParseTries match {
      case Success((userQ, sampleQ)) => correctQueries(learnerSolution, database, exercise, scenario, userQ, sampleQ)
      case Failure(error)            => SqlParseFailed(learnerSolution, error)
    }
  }

  private def correctQueries(learnerSolution: String, database: SqlExecutionDAO, exercise: SqlCompleteEx, scenario: SqlScenario, userQ: Q, sampleQ: Q) = {
    val (userTAliases, sampleTAliases) = (resolveAliases(userQ), resolveAliases(sampleQ))

    val tableComparison = compareTables(userQ, sampleQ)

    val columnComparison = compareColumns(userQ, userTAliases, sampleQ, sampleTAliases)

    val whereComparison = compareWhereClauses(userQ, userTAliases, sampleQ, sampleTAliases)

    val executionResult: SqlExecutionResult = database.executeQueries(scenario, exercise, userQ, sampleQ)

    val additionalComparisons = performAdditionalComparisons(userQ, sampleQ)

    SqlResult(learnerSolution, columnComparison, tableComparison, whereComparison, executionResult, additionalComparisons)
  }

  private def compareColumns(userQ: Q, userTAliases: Map[String, String], sampleQ: Q, sampleTAliases: Map[String, String]): MatchingResult[ColumnWrapper, ColumnMatch] =
    ColumnMatcher.doMatch(getColumnWrappers(userQ), getColumnWrappers(sampleQ))

  private def compareWhereClauses(userQ: Q, userTAliases: Map[String, String], sampleQ: Q, sampleTAliases: Map[String, String]): MatchingResult[BinaryExpression, BinaryExpressionMatch] =
    new BinaryExpressionMatcher(userTAliases, sampleTAliases).doMatch(getExpressions(userQ), getExpressions(sampleQ))

  private def getExpressions(statement: Q): Seq[BinaryExpression] = getWhere(statement) match {
    case None             => Seq[BinaryExpression]()
    case Some(expression) => new ExpressionExtractor(expression).extracted
  }

  private def compareTables(userQ: Q, sampleQ: Q): MatchingResult[Table, TableMatch] = TableMatcher.doMatch(getTables(userQ), getTables(sampleQ))

  private def resolveAliases(query: Q): Map[String, String] = getTables(query).filter(q => Option(q.getAlias).isDefined).map(t => t.getAlias.getName -> t.getName).toMap


  protected def getColumnWrappers(query: Q): Seq[ColumnWrapper]

  protected def getTables(query: Q): Seq[Table]

  protected def getWhere(query: Q): Option[Expression]

  protected def performAdditionalComparisons(userQuery: Q, sampleQuery: Q): Seq[MatchingResult[_, _ <: Match[_]]]

  // Parsing

  private def parseStatement(str: String): Try[Statement] = Try(CCJSqlParserUtil.parse(str)) match {
    case Failure(e) => Failure(new SqlStatementException(e))
    case other      => other
  }

  protected def checkStatement(statement: Statement): Try[Q] // FIXME: Failure!?!

}
