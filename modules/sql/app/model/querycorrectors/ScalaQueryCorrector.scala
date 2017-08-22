package model.querycorrectors

import scala.collection.JavaConverters._
import model.CorrectionException
import model.StringConsts
import model.conditioncorrector.ExpressionExtractor
import model.conditioncorrector.ExpressionMatch
import model.conditioncorrector.ExpressionMatcher
import model.conditioncorrector.ExtractedExpressions
import model.exercise.SqlExercise
import model.exercise.SqlSample
import model.matching.Match
import model.matching.MatchingResult
import model.matching.StringEqualsMatcher
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import play.db.Database

abstract class ScalaQueryCorrector[Q <: Statement](queryType: String) {

  val TABLE_NAME_MATCHER = new StringEqualsMatcher(StringConsts.TABLES_NAME)

  def correct(database: Database, learnerSolution: String, sampleStatement: SqlSample, exercise: SqlExercise): SqlResult = {
    val userQ = parseStatement(learnerSolution)
    val sampleQ = parseStatement(sampleStatement.sample)

    val userTableAliases = resolveAliases(userQ)
    val sampleTableAliases = resolveAliases(sampleQ)

    new SqlResult(learnerSolution)
      .setTableComparison(compareTables(userQ, sampleQ))
      .setColumnComparison(compareColumns(userQ, userTableAliases, sampleQ, sampleTableAliases))
      .setWhereComparison(compareWhereClauses(userQ, userTableAliases, sampleQ, sampleTableAliases))
      .setExecutionResult(executeQuery(database, userQ, sampleQ, exercise))
      .setOtherComparisons(makeOtherComparisons(userQ, sampleQ).asJava)
  }

  def compareColumns(userQuery: Q, userTableAliases: Map[String, String], sampleQuery: Q, sampleTableAliases: Map[String, String]) =
    new ColumnMatcher().doMatch(getColumnWrappers(userQuery).asJava, getColumnWrappers(sampleQuery).asJava)

  def compareWhereClauses(userQ: Q, userTableAliases: Map[String, String], sampleQ: Q, sampleQueryAliases: Map[String, String]) = {
    val userExps = getExpressions(userQ)
    val sampleExps = getExpressions(sampleQ)

    if (userExps.isEmpty() && sampleExps.isEmpty())
      null

    val binExMatcher = new ExpressionMatcher(userTableAliases.asJava, sampleQueryAliases.asJava)
    binExMatcher.matchExpressions(userExps, sampleExps)
  }

  def getExpressions(statement: Q) = new ExpressionExtractor(getWhere(statement)).extract()

  def compareTables(userQ: Q, sampleQ: Q) = TABLE_NAME_MATCHER.doMatch(getTableNames(userQ).asJava, getTableNames(sampleQ).asJava)
  
  def parseStatement(statement: String) = try {
    CCJSqlParserUtil.parse(statement).asInstanceOf[Q]
  } catch {
    case e: JSQLParserException => throw new CorrectionException(statement, "Es gab einen Fehler beim Parsen des Statements: " + statement, e)
    case e: ClassCastException => throw new CorrectionException(statement, "Das Statement war vom falschen Typ! Erwartet wurde " + queryType + "!", e)
  }

  def resolveAliases(query: Q): Map[String, String] = getTables(query)
    .filter(table => table != null && table.getAlias() != null)
    .map(t => t.getAlias().getName() -> t.getName).toMap
  
  def executeQuery(database: Database, userStatement: Q, sampleStatement: Q, exercise: SqlExercise): SqlExecutionResult

  def getColumnWrappers(query: Q): List[ColumnWrapper]

  def getTableNames(query: Q): List[String]

  def getTables(query: Q): List[Table]

  def getWhere(query: Q): Expression

  def makeOtherComparisons(userQ: Q, sampleQ: Q): List[MatchingResult[_, _]]


}