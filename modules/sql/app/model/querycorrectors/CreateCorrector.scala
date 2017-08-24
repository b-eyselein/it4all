package model.querycorrectors;

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.Try

import model.exercise.SqlExercise
import net.sf.jsqlparser.schema.Table
import play.db.Database

object CreateCorrector extends QueryCorrector("CREATE TABLE") {

  override type Q = net.sf.jsqlparser.statement.create.table.CreateTable

  override def executeQuery(database: Database, userStatement: Q, sampleStatement: Q,
    exercise: SqlExercise): Try[SqlExecutionResult] = null

  override def getColumnWrappers(query: Q) = query.getColumnDefinitions.asScala.map(ColumnWrapper.wrap).toList

  override def getTableNames(query: Q): List[String] = List(query.getTable.toString)

  override def getTables(query: Q): List[Table] = List(query.getTable);

  override def getWhere(query: Q) = null

  override def compareGroupByElements(plainUserQuery: Q, plainSampleQuery: Q) = None

  override def compareOrderByElements(plainUserQuery: Q, plainSampleQuery: Q) = None

}
