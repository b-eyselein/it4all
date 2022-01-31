package model.tools.sql

import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.create.table.CreateTable

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object CreateDAO extends SqlExecutionDAO[CreateTable](3109) {

  override protected def executeQuery(schemaName: String, query: CreateTable): Try[SqlQueryResult] = Try(???)

}

object CreateCorrector extends QueryCorrector("CREATE TABLE") {

  override type Q = CreateTable

  override protected val sqlExecutionDAO: SqlExecutionDAO[CreateTable] = CreateDAO

  override protected def getColumnWrappers(query: Q): Seq[CreateColumnWrapper] = Option(query.getColumnDefinitions)
    .getOrElse(java.util.List.of())
    .asScala
    .toSeq
    .map { column =>
      new CreateColumnWrapper(column.getColumnName, column.getColDataType.getDataType, column.toString)
    }

  override protected def getTables(query: Q): Seq[Table] = Seq(query.getTable)

  override protected def getWhere(query: Q): Option[Expression] = None

  override protected def checkStatement(statement: Statement): Try[CreateTable] = statement match {
    case q: CreateTable => Success(q)
    case other          => Failure(WrongStatementTypeException(queryType, gotten = other.getClass.getSimpleName))
  }

}
