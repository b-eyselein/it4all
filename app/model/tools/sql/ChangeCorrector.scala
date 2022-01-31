package model.tools.sql

import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.update.Update

import scala.util.{Try, Using}

sealed abstract class ChangeDAO[Q <: Statement] extends SqlExecutionDAO[Q](3108) {

  override protected def executeQuery(schemaName: String, query: Q): Try[SqlQueryResult] = Using.Manager { use =>
    val connection = use { db(Some(schemaName)).source.createConnection() }

    connection.setAutoCommit(false)

    val statement = use { connection.prepareStatement(query.toString) }

    statement.executeUpdate()

    val validationQuery = "SELECT * FROM " + (query match {
      case upd: Update => upd.getTable
      case ins: Insert => ins.getTable
      case del: Delete => del.getTable
    }).getName

    val validationStatement = use { connection.prepareStatement(validationQuery) }

    val res = SqlQueryResult.fromResultSet(validationStatement.executeQuery())

    connection.rollback()

    res
  }

}

object InsertDAO extends ChangeDAO[Insert]

object UpdateDAO extends ChangeDAO[Update]

object DeleteDAO extends ChangeDAO[Delete]

abstract class ChangeCorrector(queryType: String) extends QueryCorrector(queryType) {

  override protected def getColumnWrappers(query: Q): Seq[ColumnWrapper] = Seq.empty

}
