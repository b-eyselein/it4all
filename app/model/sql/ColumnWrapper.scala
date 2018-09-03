package model.sql

import model.core.matching.MatchType
import net.sf.jsqlparser.expression.Alias
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.create.table.{ColDataType, ColumnDefinition}
import net.sf.jsqlparser.statement.select.{AllColumns, AllTableColumns, SelectExpressionItem, SelectItem}

import scala.language.postfixOps


abstract sealed class ColumnWrapper {

  type C

  def getColName: String

  def getRest: String

  def hasAlias: Boolean

  def canMatch(that: ColumnWrapper): Boolean = (this, that) match {
    case (arg1: SelectColumnWrapper, arg2: SelectColumnWrapper) => arg1 canMatchOther arg2
    case (arg1: ChangeColumnWrapper, arg2: ChangeColumnWrapper) => arg1 canMatchOther arg2
    case (arg1: CreateColumnWrapper, arg2: CreateColumnWrapper) => arg1 canMatchOther arg2
    case _                                                      => false
  }

  def doMatch(that: ColumnWrapper): MatchType = (this, that) match {
    case (arg1: SelectColumnWrapper, arg2: SelectColumnWrapper) => arg1 matchOther arg2
    case (arg1: ChangeColumnWrapper, arg2: ChangeColumnWrapper) => arg1 matchOther arg2
    case (arg1: CreateColumnWrapper, arg2: CreateColumnWrapper) => arg1 matchOther arg2
    case _                                                      => MatchType.UNSUCCESSFUL_MATCH
  }

}

object ColumnWrapper {

  def wrapColumn(col: Column) = ChangeColumnWrapper(col)

  def wrapColumn(colDef: ColumnDefinition) = CreateColumnWrapper(colDef)

  def wrapColumn(selItem: SelectItem) = SelectColumnWrapper(selItem)

}

final case class ChangeColumnWrapper(col: Column) extends ColumnWrapper {

  override type C = Column

  // FIXME: eventually compare aliases?
  def canMatchOther(that: ChangeColumnWrapper): Boolean = getColName == that.getColName

  override def getColName: String = col.getColumnName

  override val getRest = ""

  override def hasAlias = false

  override def toString: String = col.toString

  def matchOther(that: ChangeColumnWrapper): MatchType = MatchType.SUCCESSFUL_MATCH

}

final case class CreateColumnWrapper(col: ColumnDefinition) extends ColumnWrapper {

  override type C = ColumnDefinition

  //    private String datatypeName
  //
  //    private String message
  //
  //    private String firstColType
  //
  //    private String secondColType
  //
  //    private MatchingResult<String, Match<String>> argumentsResult
  //
  //    private boolean typesOk

  // FIXME: eventually compare aliases?
  def canMatchOther(that: CreateColumnWrapper): Boolean = getColName == that.getColName

  override def hasAlias = false

  override def getColName: String = col.getColumnName

  override def getRest: String = col.getColDataType.getDataType toUpperCase

  def matchOther(that: CreateColumnWrapper): MatchType = {
    val theArg1 = col
    val theArg2 = that.col

    //    val datatypeName = col.getColumnName

    val typesOk = compareDataTypes(theArg1.getColDataType, theArg2.getColDataType)

    // FIXME: length of datatype? i. e. VARCHAR**(20)**

    if (typesOk) MatchType.SUCCESSFUL_MATCH else MatchType.UNSUCCESSFUL_MATCH
  }

  def compareDataTypes(userType: ColDataType, sampleType: ColDataType): Boolean = {
    val firstColType = userType.getDataType toUpperCase
    val secondColType = sampleType.getDataType toUpperCase

    // Comparing datatype
    firstColType equalsIgnoreCase secondColType
    // return "Datentyp \"" + firstColType + "\" ist nicht korrekt, erwartet
    // wurde \"" + secondColType + "\"!"

    // TODO: Compare argumentslist?
    // Seq<String> userArgs = userType.getArgumentsStringList
    // Seq<String> sampleArgs = sampleType.getArgumentsStringList
    //
    // if(userArgs == null && sampleArgs == null)
    // return "Datentyp richtig spezifiziert"
    //
    // argumentsResult = Matcher.STRING_EQ_MATCHER.match("Argumente der
    // Datentyps", userArgs, sampleArgs)

    // return "Datentyp richtig spezifiziert"
  }

  override def toString: String = col.toString

}

final case class SelectColumnWrapper(col: SelectItem) extends ColumnWrapper {

  override type C = SelectItem

  override def hasAlias: Boolean = col match {
    case sei: SelectExpressionItem => Option(sei.getAlias).isDefined
    case _                         => false
  }

  private def compareAliases(maybeAlias1: Option[Alias], maybeAlias2: Option[Alias]): Boolean = (maybeAlias1, maybeAlias2) match {
    case (Some(alias1), Some(alias2)) => alias1.getName == alias2.getName
    case (Some(_), None)              => false
    case (None, Some(_))              => false
    case (None, None)                 => true
  }

  def canMatchOther(that: SelectColumnWrapper): Boolean = (this.col, that.col) match {
    case (_: AllColumns, _: AllColumns)                       => true
    case (at1: AllTableColumns, at2: AllTableColumns)         => at1.getTable.getFullyQualifiedName == at2.getTable.getFullyQualifiedName
    case (s1: SelectExpressionItem, s2: SelectExpressionItem) => s1.getExpression.toString equalsIgnoreCase s2.getExpression.toString
    case _                                                    => false
  }

  override def getColName: String = col match {
    case _: AllColumns             => "*"
    case at: AllTableColumns       => at toString
    case set: SelectExpressionItem => set.getExpression toString
  }

  override def getRest: String = col match {
    case _: AllColumns | _: AllTableColumns => ""
    case set: SelectExpressionItem          => Option(set.getAlias) map (_.toString) getOrElse ""
  }

  def matchOther(that: SelectColumnWrapper): MatchType = (this.col, that.col) match {
    case (_: AllColumns, _: AllColumns) | (_: AllTableColumns, _: AllTableColumns) => MatchType.SUCCESSFUL_MATCH

    case (selExpr1: SelectExpressionItem, selExpr2: SelectExpressionItem) =>
      if (compareAliases(Option(selExpr1.getAlias), Option(selExpr2.getAlias))) MatchType.SUCCESSFUL_MATCH else MatchType.UNSUCCESSFUL_MATCH

    case _ => MatchType.UNSUCCESSFUL_MATCH
  }

  override def toString: String = col toString


}