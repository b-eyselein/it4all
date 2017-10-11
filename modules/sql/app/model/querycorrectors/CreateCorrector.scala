package model.querycorrectors

import model.CorrectionException
import model.exercise.SqlExercise
import model.matching.MatchingResult
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.create.table.CreateTable
import net.sf.jsqlparser.statement.select.OrderByElement
import play.db.Database

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.Try

object CreateCorrector extends QueryCorrector("CREATE TABLE") {

  type Q = CreateTable

  override def executeQuery(database: Database, userStatement: Q, sampleStatement: Q,
                            exercise: SqlExercise): Try[SqlExecutionResult] = null

  override def getColumnWrappers(query: Q): List[CreateColumnWrapper] = query.getColumnDefinitions.asScala.map(ColumnWrapper.wrap).toList

  override def getTableNames(query: Q): List[String] = List(query.getTable.toString)

  override def getTables(query: Q): List[Table] = List(query.getTable)

  override def getWhere(query: Q): Expression = null

  override def compareGroupByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[MatchingResult[Expression, GroupByMatch]] = None

  override def compareOrderByElements(plainUserQuery: Q, plainSampleQuery: Q): Option[MatchingResult[OrderByElement, OrderByMatch]] = None

  def parseStatement(statement: String): CreateTable = try {
    val parsed = CCJSqlParserUtil.parse(statement)
    parsed match {
      case q: Q => q
      case _ => throw new CorrectionException(statement, s"Das Statement war vom falschen Typ! Erwartet wurde $queryType!")
    }
  } catch {
    case e: JSQLParserException => throw new CorrectionException(statement, s"Es gab einen Fehler beim Parsen des Statements: statement", e)
  }

}
