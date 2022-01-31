package model.tools.sql

import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.update.Update

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object UpdateCorrector extends ChangeCorrector("UPDATE") {

  override type Q = Update

  override protected val sqlExecutionDAO: SqlExecutionDAO[Update] = UpdateDAO

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = query.getColumns.asScala.toSeq
    .map { column => new UpdateColumnWrapper(column.getColumnName, column.toString) }

  override protected def getTables(query: Q): Seq[Table] = Seq(query.getTable)

  override protected def getWhere(query: Q): Option[Expression] = Option(query.getWhere)

  override protected def checkStatement(statement: Statement): Try[Update] = statement match {
    case q: Update => Success(q)
    case other     => Failure(WrongStatementTypeException(queryType, gotten = other.getClass.getSimpleName))
  }

  override protected def performAdditionalComparisons(userQuery: Update, sampleQuery: Update): AdditionalComparison = AdditionalComparison(None, None)

}
