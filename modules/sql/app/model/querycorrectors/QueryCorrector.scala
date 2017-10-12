package model.querycorrectors

import model.conditioncorrector.{BinaryExpressionMatch, BinaryExpressionMatcher, ExpressionExtractor}
import model.exercise.{SqlExercise, SqlExerciseType, SqlSample}
import model.matching.{Match, Matcher, MatchingResult, StringMatcher}
import model.{CorrectionException, StringConsts}
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.OrderByElement
import play.db.Database
import model.CommonUtils.RicherTry

import scala.util.{Failure, Success, Try}

object ColumnMatcher extends Matcher[ColumnWrapper, ColumnMatch](
  model.StringConsts.COLUMNS_NAME, List("Spaltenname"), _.canMatch(_), ColumnMatch)

abstract class QueryCorrector(val queryType: String) {

  protected type Q <: net.sf.jsqlparser.statement.Statement

  type AliasMap = Map[String, String]

  val TABLE_NAME_MATCHER = new StringMatcher(StringConsts.TABLES_NAME)

  def correct(database: Database, learnerSolution: String, sampleStatement: SqlSample, exercise: SqlExercise): SqlResult = {
    parseStatement(learnerSolution).zip(parseStatement(sampleStatement.sample)) match {
      case Success((userQ, sampleQ)) =>
        val (userTAliases, sampleTAliases) = (resolveAliases(userQ), resolveAliases(sampleQ))

        val tableComparison = compareTables(userQ, sampleQ)

        val columnComparison = compareColumns(userQ, userTAliases, sampleQ, sampleTAliases)

        val whereComparison = compareWhereClauses(userQ, userTAliases, sampleQ, sampleTAliases)

        val executionResult = executeQuery(database, userQ, sampleQ, exercise) match {
          case Success(execResult) => execResult
          case Failure(cause) => throw new CorrectionException(learnerSolution, s"Es gab einen Fehler bei der Ausführung des Statements $userQ", cause)
        }

        val groupByComparison = compareGroupByElements(userQ, sampleQ)
        val orderByComparison = compareOrderByElements(userQ, sampleQ)

        SqlResult(learnerSolution, columnComparison, tableComparison, whereComparison, executionResult, groupByComparison, orderByComparison)
      case Failure(_) => null
    }
  }

  def compareColumns(userQ: Q, userTAliases: AliasMap, sampleQ: Q, sampleTAliases: AliasMap): MatchingResult[ColumnWrapper, ColumnMatch] =
    ColumnMatcher.doMatch(getColumnWrappers(userQ), getColumnWrappers(sampleQ))

  def compareWhereClauses(userQ: Q, userTAliases: AliasMap, sampleQ: Q, sampleTAliases: AliasMap): MatchingResult[BinaryExpression, BinaryExpressionMatch] =
    new BinaryExpressionMatcher(userTAliases, sampleTAliases).doMatch(getExpressions(userQ), getExpressions(sampleQ))

  def getExpressions(statement: Q): List[BinaryExpression] = getWhere(statement) match {
    case None => List.empty
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

  def getCorrector(exerciseType: SqlExerciseType): QueryCorrector = exerciseType match {
    case SqlExerciseType.CREATE => CreateCorrector
    case SqlExerciseType.DELETE => DeleteCorrector
    case SqlExerciseType.INSERT => InsertCorrector
    case SqlExerciseType.SELECT => SelectCorrector
    case SqlExerciseType.UPDATE => UpdateCorrector
    case _ => throw new CorrectionException("", "")
  }

}
