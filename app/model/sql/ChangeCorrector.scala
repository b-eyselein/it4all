package model.sql

import model.sql.ColumnWrapper.wrapColumn
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.expression.operators.relational.{ExpressionList, MultiExpressionList}
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.SubSelect

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

abstract class ChangeCorrector(queryType: String) extends QueryCorrector(queryType) {

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = Seq.empty

}

object InsertCorrector extends ChangeCorrector("INSERT") {

  import net.sf.jsqlparser.statement.insert.Insert

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

  override protected def parseStatement(statement: String): Try[Insert] = Try(CCJSqlParserUtil.parse(statement)) flatMap {
    case q: Insert => Success(q)
    case _         => Failure(new Exception(s"Das Statement war vom falschen Typ! Erwartet wurde $queryType!"))
  }
}


object DeleteCorrector extends ChangeCorrector("DELETE") {

  import net.sf.jsqlparser.statement.delete.Delete

  override type Q = Delete

  override protected def getTables(query: Q): Seq[Table] = query.getTables.asScala

  override protected def getWhere(query: Q): Option[Expression] = Option(query.getWhere)

  override protected def parseStatement(statement: String): Try[Delete] = Try(CCJSqlParserUtil.parse(statement)) flatMap {
    case q: Delete => Success(q)
    case _         => Failure(new Exception(s"Das Statement war vom falschen Typ! Erwartet wurde $queryType!"))
  }

}

object UpdateCorrector extends ChangeCorrector("UPDATE") {

  import net.sf.jsqlparser.statement.update.Update

  override type Q = Update

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = query.getColumns.asScala map wrapColumn

  override protected def getTables(query: Q): Seq[Table] = query.getTables.asScala

  override protected def getWhere(query: Q): Option[Expression] = Option(query.getWhere)

  override protected def parseStatement(statement: String): Try[Update] = Try(CCJSqlParserUtil.parse(statement)) flatMap {
    case q: Update => Success(q)
    case _         => Failure(new Exception(s"Das Statement war vom falschen Typ! Erwartet wurde $queryType!"))
  }

}
