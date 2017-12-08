package model.sql

import model.core.matching.MatchingResult
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.create.table.CreateTable
import net.sf.jsqlparser.statement.select.OrderByElement
import play.db.Database

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.Try

object CreateCorrector extends QueryCorrector("CREATE TABLE") {

  override type Q = CreateTable

  override protected def getColumnWrappers(query: Q): List[CreateColumnWrapper] = query.getColumnDefinitions.asScala.map(ColumnWrapper.wrapColumn).toList

  override protected def getTableNames(query: Q): List[String] = List(query.getTable.toString)

  override protected def getTables(query: Q): List[Table] = List(query.getTable)

  override protected def getWhere(query: Q): Option[Expression] = None

  override protected def parseStatement(statement: String): Try[CreateTable] = Try(
    CCJSqlParserUtil.parse(statement) match {
      case q: CreateTable => q
      case _              => null // throw new CorrectionException(statement, s"Das Statement war vom falschen Typ! Erwartet wurde $queryType!")
    })

}
