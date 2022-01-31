package model.tools.sql

import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.delete.Delete

import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

object DeleteCorrector extends ChangeCorrector("DELETE") {

  override type Q = Delete

  override protected val sqlExecutionDAO: SqlExecutionDAO[Delete] = DeleteDAO

  override protected def getTables(query: Q): Seq[Table] = query.getTables.asScala.toSeq

  override protected def getWhere(query: Q): Option[Expression] = Option(query.getWhere)

  override protected def checkStatement(statement: Statement): Try[Delete] = statement match {
    case q: Delete => Success(q)
    case other     => Failure(WrongStatementTypeException(queryType, gotten = other.getClass.getSimpleName))
  }

  override protected def performAdditionalComparisons(userQuery: Delete, sampleQuery: Delete): AdditionalComparison = AdditionalComparison(None, None)

}
