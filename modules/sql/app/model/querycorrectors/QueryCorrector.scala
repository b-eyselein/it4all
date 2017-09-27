package model.querycorrectors

import scala.util.Failure
import scala.util.Success
import scala.util.Try

import model.CorrectionException
import model.StringConsts
import model.conditioncorrector.ExpressionExtractor
import model.exercise.SqlExercise
import model.exercise.SqlExerciseType
import model.exercise.SqlSample
import model.matching.Matcher
import model.matching.MatchingResult
import model.matching.StringMatcher
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.OrderByElement
import play.db.Database
import model.conditioncorrector.BinaryExpressionMatcher

object ColumnMatcher extends Matcher[ColumnWrapper, ColumnMatch](
  model.StringConsts.COLUMNS_NAME,
  List("Spaltenname"),
  _.canMatch(_),
  new ColumnMatch(_, _))

abstract class QueryCorrector(val queryType: String) {

  type Q <: net.sf.jsqlparser.statement.Statement

  type AliasMap = Map[String, String]

  val TABLE_NAME_MATCHER = new StringMatcher(StringConsts.TABLES_NAME)

  def correct(database: Database, learnerSolution: String, sampleStatement: SqlSample, exercise: SqlExercise) = {
    val userQ = parseStatement(learnerSolution)
    val sampleQ = parseStatement(sampleStatement.sample)

    val (userTAliases, sampleTAliases) = (resolveAliases(userQ), resolveAliases(sampleQ))

    val tableComparison = compareTables(userQ, sampleQ)

    val columnComparison = compareColumns(userQ, userTAliases, sampleQ, sampleTAliases)

    val whereComparison = compareWhereClauses(userQ, userTAliases, sampleQ, sampleTAliases)

    val executionResult = executeQuery(database, userQ, sampleQ, exercise) match {
      case Success(executionResult) => executionResult
      case Failure(cause) => throw new CorrectionException(learnerSolution, s"Es gab einen Fehler bei der Ausführung des Statements $userQ", cause)
    }

    val groupByComparison = compareGroupByElements(userQ, sampleQ)
    val orderByComparison = compareOrderByElements(userQ, sampleQ)

    new SqlResult(learnerSolution, columnComparison, tableComparison, whereComparison, executionResult, groupByComparison, orderByComparison)
  }

  def compareColumns(userQ: Q, userTAliases: AliasMap, sampleQ: Q, sampleTAliases: AliasMap) =
    ColumnMatcher.doMatch(getColumnWrappers(userQ), getColumnWrappers(sampleQ))

  def compareWhereClauses(userQ: Q, userTAliases: AliasMap, sampleQ: Q, sampleTAliases: AliasMap) =
    new BinaryExpressionMatcher(userTAliases, sampleTAliases).doMatch(getExpressions(userQ), getExpressions(sampleQ))

  def getExpressions(statement: Q) = new ExpressionExtractor(getWhere(statement)).extracted

  def compareTables(userQ: Q, sampleQ: Q) = TABLE_NAME_MATCHER.doMatch(getTableNames(userQ), getTableNames(sampleQ))

  def parseStatement(statement: String): Q

  def resolveAliases(query: Q) = getTables(query).filter(_.getAlias != null).map(t => t.getAlias.getName -> t.getName).toMap

  def executeQuery(database: Database, userStatement: Q, sampleStatement: Q, exercise: SqlExercise): Try[SqlExecutionResult]

  def getColumnWrappers(query: Q): List[ColumnWrapper]

  def getTableNames(query: Q): List[String]

  def getTables(query: Q): List[Table]

  def getWhere(query: Q): Expression

  def compareGroupByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[MatchingResult[Expression, GroupByMatch]]

  def compareOrderByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[MatchingResult[OrderByElement, OrderByMatch]]

}

object QueryCorrector {

  def getCorrector(exerciseType: SqlExerciseType) = exerciseType match {
    case SqlExerciseType.CREATE => CreateCorrector
    case SqlExerciseType.DELETE => DeleteCorrector
    case SqlExerciseType.INSERT => InsertCorrector
    case SqlExerciseType.SELECT => SelectCorrector
    case SqlExerciseType.UPDATE => UpdateCorrector
    case _ => throw new CorrectionException("", "")
  }

}
