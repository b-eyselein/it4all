package model.tools.sql

import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.create.table.CreateTable
import play.api.Logger

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object CreateCorrector extends QueryCorrector("CREATE TABLE") {

  override protected val logger: Logger = Logger(CreateCorrector.getClass)

  override type Q = CreateTable

  override protected def getColumnWrappers(query: Q): Seq[CreateColumnWrapper] =
    query.getColumnDefinitions.asScala.map { column => CreateColumnWrapper(column.getColumnName, column) }.toSeq

  override protected def getTables(query: Q): Seq[Table] = Seq(query.getTable)

  override protected def getWhere(query: Q): Option[Expression] = None

  override protected def checkStatement(statement: Statement): Try[CreateTable] =
    statement match {
      case q: CreateTable   => Success(q)
      case other: Statement => Failure(WrongStatementTypeException(queryType, gotten = other.getClass.getSimpleName))
    }

  override protected def performAdditionalComparisons(
    userQuery: CreateTable,
    sampleQuery: CreateTable
  ): AdditionalComparison =
    AdditionalComparison(None, None)

}
