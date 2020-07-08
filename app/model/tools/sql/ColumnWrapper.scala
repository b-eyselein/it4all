package model.tools.sql

import model.matching.MatchType
import net.sf.jsqlparser.expression.Alias
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.create.table.ColumnDefinition
import net.sf.jsqlparser.statement.select.{AllColumns, AllTableColumns, SelectExpressionItem, SelectItem}

abstract sealed class ColumnWrapper {

  def columnName: String

  def getAlias: Option[String]

  def canMatch(that: ColumnWrapper): Boolean =
    (this, that) match {
      case (arg1: SelectColumnWrapper, arg2: SelectColumnWrapper) => arg1 canMatchOther arg2
      case (arg1: ChangeColumnWrapper, arg2: ChangeColumnWrapper) => arg1.columnName == arg2.columnName
      case (arg1: CreateColumnWrapper, arg2: CreateColumnWrapper) => arg1.columnName == arg2.columnName
      case _                                                      => false
    }

  def doMatch(that: ColumnWrapper): MatchType =
    (this, that) match {
      case (arg1: SelectColumnWrapper, arg2: SelectColumnWrapper) => arg1 matchOther arg2
      case (arg1: CreateColumnWrapper, arg2: CreateColumnWrapper) => arg1 matchOther arg2
      case (_: ChangeColumnWrapper, _: ChangeColumnWrapper)       => MatchType.SUCCESSFUL_MATCH
      case _                                                      => MatchType.UNSUCCESSFUL_MATCH
    }

}

final case class ChangeColumnWrapper(columnName: String, private val col: Column) extends ColumnWrapper {

  override def getAlias: Option[String] = None

  override def toString: String = col.toString

}

final case class CreateColumnWrapper(columnName: String, private val col: ColumnDefinition) extends ColumnWrapper {

  override def getAlias: Option[String] = None

  override def toString: String = col.toString

  def getRest: String = col.getColDataType.getDataType.toUpperCase

  def matchOther(that: CreateColumnWrapper): MatchType = {
    val userColType   = this.col.getColDataType.getDataType.toUpperCase
    val sampleColType = that.col.getColDataType.getDataType.toUpperCase

    if (userColType.equalsIgnoreCase(sampleColType)) {
      MatchType.SUCCESSFUL_MATCH
    } else {
      MatchType.UNSUCCESSFUL_MATCH
    }
  }

}

final case class SelectColumnWrapper(columnName: String, private val col: SelectItem) extends ColumnWrapper {

  override def getAlias: Option[String] =
    col match {
      case sei: SelectExpressionItem => Option(sei.getAlias).map(_.getName)
      case _                         => None
    }

  private def compareAliases(maybeAlias1: Option[Alias], maybeAlias2: Option[Alias]): Boolean =
    (maybeAlias1, maybeAlias2) match {
      case (Some(alias1), Some(alias2)) => alias1.getName == alias2.getName
      case (Some(_), None)              => false
      case (None, Some(_))              => false
      case (None, None)                 => true
    }

  def canMatchOther(that: SelectColumnWrapper): Boolean =
    (this.col, that.col) match {
      case (_: AllColumns, _: AllColumns) => true
      case (at1: AllTableColumns, at2: AllTableColumns) =>
        at1.getTable.getFullyQualifiedName == at2.getTable.getFullyQualifiedName
      case (s1: SelectExpressionItem, s2: SelectExpressionItem) =>
        s1.getExpression.toString equalsIgnoreCase s2.getExpression.toString
      case _ => false
    }

  def matchOther(that: SelectColumnWrapper): MatchType =
    (this.col, that.col) match {
      case (_: AllColumns, _: AllColumns) | (_: AllTableColumns, _: AllTableColumns) => MatchType.SUCCESSFUL_MATCH

      case (selExpr1: SelectExpressionItem, selExpr2: SelectExpressionItem) =>
        if (compareAliases(Option(selExpr1.getAlias), Option(selExpr2.getAlias))) MatchType.SUCCESSFUL_MATCH
        else MatchType.UNSUCCESSFUL_MATCH

      case _ => MatchType.UNSUCCESSFUL_MATCH
    }

  override def toString: String = col.toString

}
