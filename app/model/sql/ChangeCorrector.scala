package model.sql

import java.sql.Connection

import model.core.CommonUtils.using
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table

import ColumnWrapper.wrapColumn

import scala.collection.JavaConverters._
import scala.util.Try

abstract class ChangeCorrector(queryType: String) extends QueryCorrector(queryType) {

  def runUpdate(conn: Connection, query: String): Int = using(conn.createStatement)(_.executeUpdate(query))

  def runValidationQuery(conn: Connection, query: String): SqlQueryResult =
    using(conn.createStatement)(s => new SqlQueryResult(s.executeQuery(query)))

  def getResultSet(statement: Q, connection: Connection, validation: String): SqlQueryResult = {
    runUpdate(connection, statement.toString)
    val result = runValidationQuery(connection, validation)
    connection.rollback()
    result
  }

}

object InsertCorrector extends ChangeCorrector("INSERT") {

  import net.sf.jsqlparser.statement.insert.Insert

  override type Q = Insert

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = Seq.empty

  override protected def getTables(query: Q) = Seq(query.getTable)

  override protected def getWhere(query: Q): Option[Expression] = None

  override protected def parseStatement(statement: String): Try[Insert] = Try(
    CCJSqlParserUtil.parse(statement) match {
      case q: Insert => q
      case _         => null // throw new CorrectionException(statement, s"Das Statement war vom falschen Typ! Erwartet wurde $queryType!")
    })
}


object DeleteCorrector extends ChangeCorrector("DELETE") {

  import net.sf.jsqlparser.statement.delete.Delete

  override type Q = Delete

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = Seq.empty

  override protected def getTables(query: Q): Seq[Table] = query.getTables.asScala

  override protected def getWhere(query: Q): Option[Expression] = Option(query.getWhere)

  override protected def parseStatement(statement: String): Try[Delete] = Try(
    CCJSqlParserUtil.parse(statement) match {
      case q: Delete => q
      case _         => null // Left(s"Das Statement war vom falschen Typ! Erwartet wurde $queryType!")
    })

}

object UpdateCorrector extends ChangeCorrector("UPDATE") {

  import net.sf.jsqlparser.statement.update.Update

  override type Q = Update

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = query.getColumns.asScala map wrapColumn

  override protected def getTables(query: Q): Seq[Table] = query.getTables.asScala

  override protected def getWhere(query: Q): Option[Expression] = Option(query.getWhere)

  override protected def parseStatement(statement: String): Try[Update] = Try(
    CCJSqlParserUtil.parse(statement) match {
      case q: Update => q
      case _         => null //  throw new CorrectionException(statement, s"Das Statement war vom falschen Typ! Erwartet wurde $queryType!")
    })

}
