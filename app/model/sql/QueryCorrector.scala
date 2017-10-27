package model.sql

import model.core.CommonUtils.RicherTry
import model.core.StringConsts._
import model.core.matching.{Match, Matcher, MatchingResult, StringMatcher}
import model.sql.SqlEnums.SqlExerciseType
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.OrderByElement
import play.db.Database

import scala.util.{Failure, Success, Try}

object ColumnMatcher extends Matcher[ColumnWrapper, ColumnMatch](
  COLUMNS_NAME, List("Spaltenname"), _.canMatch(_), ColumnMatch)

abstract class QueryCorrector(val queryType: String) {

  protected type Q <: net.sf.jsqlparser.statement.Statement

  type AliasMap = Map[String, String]

  val TABLE_NAME_MATCHER = new StringMatcher(TABLES_NAME)

  def correct(database: Database, learnerSolution: String, sampleStatement: SqlSample, exercise: SqlExercise): SqlResult = {
    parseStatement(learnerSolution).zip(parseStatement(sampleStatement.sample)) match {
      case Success((userQ, sampleQ)) =>
        val (userTAliases, sampleTAliases) = (resolveAliases(userQ), resolveAliases(sampleQ))

        val tableComparison = compareTables(userQ, sampleQ)

        val columnComparison = compareColumns(userQ, userTAliases, sampleQ, sampleTAliases)

        val whereComparison = compareWhereClauses(userQ, userTAliases, sampleQ, sampleTAliases)

        val executionResult = executeQuery(database, userQ, sampleQ, exercise) match {
          case Success(execResult) => Some(execResult)
          case Failure(cause)      => None
        }

        val groupByComparison = compareGroupByElements(userQ, sampleQ)
        val orderByComparison = compareOrderByElements(userQ, sampleQ)

        SqlResult(learnerSolution, columnComparison, tableComparison, whereComparison, executionResult, groupByComparison, orderByComparison)
      case Failure(_)                => null
    }
  }

  def compareColumns(userQ: Q, userTAliases: AliasMap, sampleQ: Q, sampleTAliases: AliasMap): MatchingResult[ColumnWrapper, ColumnMatch] =
    ColumnMatcher.doMatch(getColumnWrappers(userQ), getColumnWrappers(sampleQ))

  def compareWhereClauses(userQ: Q, userTAliases: AliasMap, sampleQ: Q, sampleTAliases: AliasMap): MatchingResult[BinaryExpression, BinaryExpressionMatch] =
    new BinaryExpressionMatcher(userTAliases, sampleTAliases).doMatch(getExpressions(userQ), getExpressions(sampleQ))

  def getExpressions(statement: Q): List[BinaryExpression] = getWhere(statement) match {
    case None             => List.empty
    case Some(expression) => new ExpressionExtractor(expression).extracted

  }

  def compareTables(userQ: Q, sampleQ: Q): MatchingResult[String, Match[String]] = TABLE_NAME_MATCHER.doMatch(getTableNames(userQ), getTableNames(sampleQ))

  def resolveAliases(query: Q): Map[String, String] = getTables(query).filter(_.getAlias != null).map(t => t.getAlias.getName -> t.getName).toMap

  protected def parseStatement(statement: String): Try[Q]

  protected def executeQuery(database: Database, userStatement: Q, sampleStatement: Q, exercise: SqlExercise): Try[SqlExecutionResult]

  protected def getColumnWrappers(query: Q): List[ColumnWrapper]

  protected def getTableNames(query: Q): List[String]

  protected def getTables(query: Q): List[Table]

  protected def getWhere(query: Q): Option[Expression]

  protected def compareGroupByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[MatchingResult[Expression, GroupByMatch]]

  protected def compareOrderByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[MatchingResult[OrderByElement, OrderByMatch]]

}

object QueryCorrector {

  val correctors: Map[SqlExerciseType, QueryCorrector] = Map(
    SqlExerciseType.CREATE -> CreateCorrector,
    SqlExerciseType.DELETE -> DeleteCorrector,
    SqlExerciseType.INSERT -> InsertCorrector,
    SqlExerciseType.SELECT -> SelectCorrector,
    SqlExerciseType.UPDATE -> UpdateCorrector
  )

}
