package model.sql

import java.sql.Connection

import model.core.CommonUtils.cleanly
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table

import scala.collection.JavaConverters._
import scala.util.Try

abstract class ChangeCorrector(queryType: String) extends QueryCorrector(queryType) {

  def runUpdate(conn: Connection, query: String): Try[Int] = cleanly(conn.createStatement)(_.close)(_.executeUpdate(query))

  def runValidationQuery(conn: Connection, query: String): Try[SqlQueryResult] =
    cleanly(conn.createStatement)(_.close)(s => new SqlQueryResult(s.executeQuery(query)))

  def getResultSet(statement: Q, connection: Connection, validation: String): Try[SqlQueryResult] = {
    runUpdate(connection, statement.toString)
    val result = runValidationQuery(connection, validation)
    connection.rollback()
    result
  }

}

object InsertCorrector extends ChangeCorrector("INSERT") {

  import net.sf.jsqlparser.statement.insert.Insert

  override type Q = Insert

  override protected def getColumnWrappers(query: Q): List[ColumnWrapper] = List.empty

  override protected def getTableNames(query: Q) = List(query.getTable.getName)

  override protected def getTables(query: Q) = List(query.getTable)

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

  override protected def getColumnWrappers(query: Q): List[ColumnWrapper] = List.empty

  override protected def getTableNames(query: Q) = List(query.getTable.getName)

  override protected def getTables(query: Q): List[Table] = query.getTables.asScala.toList

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

  override protected def getColumnWrappers(query: Q): List[ColumnWrapper] = query.getColumns.asScala.map(ColumnWrapper.wrapColumn).toList

  override protected def getTableNames(query: Q): List[String] = query.getTables.asScala.map(_.getName).toList

  override protected def getTables(query: Q): List[Table] = query.getTables.asScala.toList

  override protected def getWhere(query: Q): Option[Expression] = Option(query.getWhere)

  override protected def parseStatement(statement: String): Try[Update] = Try(
    CCJSqlParserUtil.parse(statement) match {
      case q: Update => q
      case _         => null //  throw new CorrectionException(statement, s"Das Statement war vom falschen Typ! Erwartet wurde $queryType!")
    })

}
