package model.querycorrectors

import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.collection.JavaConverters.seqAsJavaListConverter

import model.CorrectionException
import model.StringConsts
import model.conditioncorrector.ExpressionExtractor
import model.conditioncorrector.ExpressionMatcher
import model.exercise.SqlExercise
import model.exercise.SqlSample
import model.matching.ScalaMatchingResult
import model.matching.StringEqualsMatcher
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import play.db.Database
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import model.matching.ScalaMatchingResult
import model.matching.ScalaStringMatcher

abstract class ScalaQueryCorrector(queryType: String) {

  type Q <: net.sf.jsqlparser.statement.Statement

  type AliasMap = Map[String, String]

  val TABLE_NAME_MATCHER = new ScalaStringMatcher(StringConsts.TABLES_NAME)

  def correct(database: Database, learnerSolution: String, sampleStatement: SqlSample, exercise: SqlExercise): ScalaSqlResult = {
    val userQ = parseStatement(learnerSolution)
    val sampleQ = parseStatement(sampleStatement.sample)

    val (userTAliases, sampleTAliases) = (resolveAliases(userQ), resolveAliases(sampleQ))

    new ScalaSqlResult(learnerSolution,
      tableComparison = compareTables(userQ, sampleQ),
      columnComparison = compareColumns(userQ, userTAliases, sampleQ, sampleTAliases),
      whereComparison = compareWhereClauses(userQ, userTAliases, sampleQ, sampleTAliases),
      executionResult = executeQuery(database, userQ, sampleQ, exercise) match {
        case Success(executionResult) => executionResult
        case Failure(cause) => throw new CorrectionException(learnerSolution, "Es gab einen Fehler bei der Ausführung des Statements ", cause)
      },
      otherComparisons = makeOtherComparisons(userQ, sampleQ))
  }

  def compareColumns(userQ: Q, userTAliases: AliasMap, sampleQ: Q, sampleTAliases: AliasMap) =
    new ColumnMatcher().doMatch(getColumnWrappers(userQ), getColumnWrappers(sampleQ))

  def compareWhereClauses(userQ: Q, userTAliases: AliasMap, sampleQ: Q, sampleTAliases: AliasMap) =
    new ExpressionMatcher(userTAliases, sampleTAliases).matchExpressions(getExpressions(userQ), getExpressions(sampleQ))

  def getExpressions(statement: Q) = new ExpressionExtractor(getWhere(statement)).extract()

  def compareTables(userQ: Q, sampleQ: Q) = TABLE_NAME_MATCHER.doMatch(getTableNames(userQ), getTableNames(sampleQ))

  def parseStatement(statement: String) = try { CCJSqlParserUtil.parse(statement).asInstanceOf[Q] } catch {
    case e: JSQLParserException => throw new CorrectionException(statement, "Es gab einen Fehler beim Parsen des Statements: " + statement, e)
    case e: ClassCastException => throw new CorrectionException(statement, "Das Statement war vom falschen Typ! Erwartet wurde " + queryType + "!", e)
  }

  def resolveAliases(query: Q): AliasMap = getTables(query)
    .filter(table => table != null && table.getAlias != null).map(t => t.getAlias.getName -> t.getName).toMap

  def executeQuery(database: Database, userStatement: Q, sampleStatement: Q, exercise: SqlExercise): Try[SqlExecutionResult]

  def getColumnWrappers(query: Q): List[ScalaColumnWrapper]

  def getTableNames(query: Q): List[String]

  def getTables(query: Q): List[Table]

  def getWhere(query: Q): Expression

  def makeOtherComparisons(userQ: Q, sampleQ: Q): List[ScalaMatchingResult[_, _]]

}
