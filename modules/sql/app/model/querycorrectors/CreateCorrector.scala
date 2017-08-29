package model.querycorrectors;

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.Try

import model.exercise.SqlExercise
import net.sf.jsqlparser.schema.Table
import play.db.Database
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import model.CorrectionException
import net.sf.jsqlparser.JSQLParserException

object CreateCorrector extends QueryCorrector("CREATE TABLE") {

  type Q = net.sf.jsqlparser.statement.create.table.CreateTable

  override def executeQuery(database: Database, userStatement: Q, sampleStatement: Q,
    exercise: SqlExercise): Try[SqlExecutionResult] = null

  override def getColumnWrappers(query: Q) = query.getColumnDefinitions.asScala.map(ColumnWrapper.wrap).toList

  override def getTableNames(query: Q): List[String] = List(query.getTable.toString)

  override def getTables(query: Q): List[Table] = List(query.getTable);

  override def getWhere(query: Q) = null

  override def compareGroupByElements(plainUserQuery: Q, plainSampleQuery: Q) = None

  override def compareOrderByElements(plainUserQuery: Q, plainSampleQuery: Q) = None

  def parseStatement(statement: String) = try {
    val parsed = CCJSqlParserUtil.parse(statement)
    parsed match {
      case q: Q => q
      case _ => throw new CorrectionException(statement, "Das Statement war vom falschen Typ! Erwartet wurde " + queryType + "!")
    }
  } catch {
    case e: JSQLParserException => throw new CorrectionException(statement, "Es gab einen Fehler beim Parsen des Statements: " + statement, e)
  }

}
