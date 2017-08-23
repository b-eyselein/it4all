package model.querycorrectors.create;

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.Try

import model.exercise.SqlExercise
import model.querycorrectors.ColumnWrapper
import model.querycorrectors.ScalaQueryCorrector
import model.querycorrectors.SqlExecutionResult
import net.sf.jsqlparser.schema.Table
import play.db.Database

object ScalaCreateCorrector extends ScalaQueryCorrector("CREATE TABLE") {

  override type Q = net.sf.jsqlparser.statement.create.table.CreateTable

  override def executeQuery(database: Database, userStatement: Q, sampleStatement: Q,
    exercise: SqlExercise): Try[SqlExecutionResult] = null

  override def getColumnWrappers(query: Q) = query.getColumnDefinitions.asScala.map(ColumnWrapper.wrap).toList

  override def getTableNames(query: Q): List[String] = List(query.getTable.toString)

  override def getTables(query: Q): List[Table] = List(query.getTable);

  override def getWhere(query: Q) = null

  override def makeOtherComparisons(userQ: Q, sampleQ: Q) = List.empty

}
