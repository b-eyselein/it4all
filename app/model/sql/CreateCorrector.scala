package model.sql

import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.create.table.CreateTable

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{Failure, Success, Try}

object CreateCorrector extends QueryCorrector("CREATE TABLE") {

  override type Q = CreateTable

  override protected def getColumnWrappers(query: Q): Seq[CreateColumnWrapper] = query.getColumnDefinitions.asScala map ColumnWrapper.wrapColumn

  override protected def getTables(query: Q): Seq[Table] = Seq(query.getTable)

  override protected def getWhere(query: Q): Option[Expression] = None

  override protected def parseStatement(statement: String): Try[CreateTable] = Try(CCJSqlParserUtil.parse(statement)) flatMap {
    case q: CreateTable => Success(q)
    case _              => Failure(new Exception(s"Das Statement war vom falschen Typ! Erwartet wurde $queryType!"))
  }

}
