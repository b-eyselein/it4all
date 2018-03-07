package model.sql

import model.sql.ColumnWrapper.wrapColumn
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.expression.operators.relational.{ExpressionList, MultiExpressionList}
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.select.SubSelect
import net.sf.jsqlparser.statement.update.Update

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

abstract class ChangeCorrector(queryType: String) extends QueryCorrector(queryType) {

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = Seq.empty

}

object InsertCorrector extends ChangeCorrector("INSERT") {


  override type Q = Insert

  // FIXME: correct inserted values!
  protected def correctValues(query: Q): Boolean = {
    query.getItemsList match {
      case mel: MultiExpressionList => false
      case el: ExpressionList       => false
      case sub: SubSelect           => false
    }
    ???
  }

  override protected def getTables(query: Q) = Seq(query.getTable)

  override protected def getWhere(query: Q): Option[Expression] = None

  override protected def checkStatement(statement: Statement): Try[Insert] = statement match {
    case q: Insert        => Success(q)
    case other: Statement => Failure(WrongStatementTypeException(queryType, gotten = other.getClass.getSimpleName))
  }

}


object DeleteCorrector extends ChangeCorrector("DELETE") {


  override type Q = Delete

  override protected def getTables(query: Q): Seq[Table] = query.getTables.asScala

  override protected def getWhere(query: Q): Option[Expression] = Option(query.getWhere)

  override protected def checkStatement(statement: Statement): Try[Delete] = statement match {
    case q: Delete        => Success(q)
    case other: Statement => Failure(WrongStatementTypeException(queryType, gotten = other.getClass.getSimpleName))
  }

}

object UpdateCorrector extends ChangeCorrector("UPDATE") {


  override type Q = Update

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = query.getColumns.asScala map wrapColumn

  override protected def getTables(query: Q): Seq[Table] = query.getTables.asScala

  override protected def getWhere(query: Q): Option[Expression] = Option(query.getWhere)

  override protected def checkStatement(statement: Statement): Try[Update] = statement match {
    case q: Update        => Success(q)
    case other: Statement => Failure(WrongStatementTypeException(queryType, gotten = other.getClass.getSimpleName))
  }

}
