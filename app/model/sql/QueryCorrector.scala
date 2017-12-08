package model.sql

import model.core.CommonUtils.RicherTry
import model.core.matching._
import model.sql.SqlConsts._
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement

import scala.util.{Failure, Success, Try}

abstract class QueryCorrector(val queryType: String) {

  protected type Q <: net.sf.jsqlparser.statement.Statement

  type AliasMap = Map[String, String]

  val TABLE_NAME_MATCHER = new StringMatcher(TablesName)

  protected def parseSql(sql: String): Try[Statement] = Try(CCJSqlParserUtil.parse(sql))

  def correct(database: SqlExecutionDAO, learnerSolution: String, sampleStatement: SqlSample, exercise: SqlCompleteEx, scenario: SqlScenario): SqlCorrResult = {
    parseStatement(learnerSolution).zip(parseStatement(sampleStatement.sample)) match {
      case Success((userQ, sampleQ)) =>
        val (userTAliases, sampleTAliases) = (resolveAliases(userQ), resolveAliases(sampleQ))

        val tableComparison = compareTables(userQ, sampleQ)

        val columnComparison = compareColumns(userQ, userTAliases, sampleQ, sampleTAliases)

        val whereComparison = compareWhereClauses(userQ, userTAliases, sampleQ, sampleTAliases)

        val executionResult = database.executeQuery(scenario.shortName, userQ.toString, sampleQ.toString)

        val groupByComparison = compareGroupByElements(userQ, sampleQ)
        val orderByComparison = compareOrderByElements(userQ, sampleQ)

        SqlResult(learnerSolution, columnComparison, tableComparison, whereComparison, Some(executionResult), groupByComparison, orderByComparison)
      case Failure(_)                => SqlFailed(learnerSolution)
    }
  }

  def compareColumns(userQ: Q, userTAliases: AliasMap, sampleQ: Q, sampleTAliases: AliasMap): ColumnMatchingResult =
    ColumnMatcher.doMatch(getColumnWrappers(userQ), getColumnWrappers(sampleQ))

  def compareWhereClauses(userQ: Q, userTAliases: AliasMap, sampleQ: Q, sampleTAliases: AliasMap): BinaryExpressionMatchingResult =
    new BinaryExpressionMatcher(userTAliases, sampleTAliases).doMatch(getExpressions(userQ), getExpressions(sampleQ))

  def getExpressions(statement: Q): Seq[BinaryExpression] = getWhere(statement) match {
    case None             => Seq.empty
    case Some(expression) => new ExpressionExtractor(expression).extracted
  }

  def compareTables(userQ: Q, sampleQ: Q): StringMatchingResult = TABLE_NAME_MATCHER.doMatch(getTableNames(userQ), getTableNames(sampleQ))

  def resolveAliases(query: Q): Map[String, String] = getTables(query).filter(_.getAlias != null).map(t => t.getAlias.getName -> t.getName).toMap

  protected def parseStatement(statement: String): Try[Q]

  protected def getColumnWrappers(query: Q): Seq[ColumnWrapper]

  protected def getTableNames(query: Q): Seq[String]

  protected def getTables(query: Q): Seq[Table]

  protected def getWhere(query: Q): Option[Expression]

  protected def compareGroupByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[GroupByMatchingResult] = None

  protected def compareOrderByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[OrderByMatchingResult] = None

}
