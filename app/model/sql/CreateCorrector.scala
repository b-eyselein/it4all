package model.sql

import model.core.matching.{Match, MatchingResult}
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.create.table.CreateTable

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{Failure, Success, Try}

object CreateCorrector extends QueryCorrector("CREATE TABLE") {

  override type Q = CreateTable

  override protected def getColumnWrappers(query: Q): Seq[CreateColumnWrapper] = query.getColumnDefinitions.asScala map ColumnWrapper.wrapColumn

  override protected def getTables(query: Q): Seq[Table] = Seq(query.getTable)

  override protected def getWhere(query: Q): Option[Expression] = None

  override protected def checkStatement(statement: Statement): Try[CreateTable] = statement match {
    case q: CreateTable   => Success(q)
    case other: Statement => Failure(WrongStatementTypeException(queryType, gotten = other.getClass.getSimpleName))
  }

  override protected def performAdditionalComparisons(userQuery: CreateTable, sampleQuery: CreateTable): Seq[MatchingResult[_, _ <: Match[_]]] = Seq.empty

}
