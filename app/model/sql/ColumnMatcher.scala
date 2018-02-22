package model.sql

import model.Enums.MatchType
import model.Enums.MatchType._
import model.core.matching.{Match, Matcher, MatchingResult}
import net.sf.jsqlparser.expression.Alias
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.create.table.{ColDataType, ColumnDefinition}
import net.sf.jsqlparser.statement.select.{AllColumns, AllTableColumns, SelectExpressionItem, SelectItem}
import play.api.libs.json.Json

import scala.language.postfixOps

case class ColumnMatch(userArg: Option[ColumnWrapper], sampleArg: Option[ColumnWrapper])
  extends Match[ColumnWrapper] {

  val hasAlias: Boolean = (userArg exists (_.hasAlias)) || (sampleArg exists (_.hasAlias))

  val restMatched: Boolean = false

  val colNamesMatched: Boolean = matchType == SUCCESSFUL_MATCH || matchType == UNSUCCESSFUL_MATCH

  val firstColName: String = userArg map (_.getColName) getOrElse ""

  val firstRest: String = userArg map (_.getRest) getOrElse ""

  val secondColName: String = sampleArg map (_.getColName) getOrElse ""

  val secondRest: String = sampleArg map (_.getRest) getOrElse ""

  override def analyze(userArg: ColumnWrapper, sampleArg: ColumnWrapper): MatchType = userArg doMatch sampleArg

}

case class ColumnMatchingResult(allMatches: Seq[ColumnMatch]) extends MatchingResult[ColumnWrapper, ColumnMatch] {

  override val matchName: String = "Spalten"

}

object ColumnMatcher extends Matcher[ColumnWrapper, ColumnMatch, ColumnMatchingResult] {

  override def canMatch: (ColumnWrapper, ColumnWrapper) => Boolean = _ canMatch _

  override def matchInstantiation: (Option[ColumnWrapper], Option[ColumnWrapper]) => ColumnMatch = ColumnMatch

  override def resultInstantiation: Seq[ColumnMatch] => ColumnMatchingResult = ColumnMatchingResult

}

abstract sealed class ColumnWrapper {

  type C

  def getColName: String

  def getRest: String

  def hasAlias: Boolean

  def canMatch(that: ColumnWrapper): Boolean = (this, that) match {
    case (arg1: SelectColumnWrapper, arg2: SelectColumnWrapper) => arg1 canMatchOther arg2
    case (arg1: ChangeColumnWrapper, arg2: ChangeColumnWrapper) => arg1 canMatchOther arg2
    case (arg1: CreateColumnWrapper, arg2: CreateColumnWrapper) => arg1 canMatchOther arg2
    case _                                                      => false // throw new CorrectionException("", "Fehler bei Vergleich von zwei ColumnWrappers!")
  }

  def doMatch(that: ColumnWrapper): MatchType = (this, that) match {
    case (arg1: SelectColumnWrapper, arg2: SelectColumnWrapper) => arg1 matchOther arg2
    case (arg1: ChangeColumnWrapper, arg2: ChangeColumnWrapper) => arg1 matchOther arg2
    case (arg1: CreateColumnWrapper, arg2: CreateColumnWrapper) => arg1 matchOther arg2
    case _                                                      => null // throw new CorrectionException("", "Fehler bei Matching von zwei ColumnWrappers!")
  }

}

object ColumnWrapper {

  def wrapColumn(col: Column) = ChangeColumnWrapper(col)

  def wrapColumn(colDef: ColumnDefinition) = CreateColumnWrapper(colDef)

  def wrapColumn(selItem: SelectItem) = SelectColumnWrapper(selItem)

}

case class ChangeColumnWrapper(col: Column) extends ColumnWrapper {

  override type C = Column

  // FIXME: eventually compare aliases?
  def canMatchOther(that: ChangeColumnWrapper): Boolean = getColName == that.getColName

  override def getColName: String = col.getColumnName

  override val getRest = ""

  override def hasAlias = false

  override def toString: String = col.toString

  def matchOther(that: ChangeColumnWrapper): MatchType = SUCCESSFUL_MATCH

}

case class CreateColumnWrapper(col: ColumnDefinition) extends ColumnWrapper {

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

    if (typesOk) SUCCESSFUL_MATCH else UNSUCCESSFUL_MATCH
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

case class SelectColumnWrapper(col: SelectItem) extends ColumnWrapper {

  override type C = SelectItem

  override def hasAlias: Boolean = col match {
    case sei: SelectExpressionItem => sei.getAlias != null
    case _                         => false
  }

  def compareAliases(maybeAlias1: Option[Alias], maybeAlias2: Option[Alias]): Boolean = (maybeAlias1, maybeAlias2) match {
    case (Some(alias1), Some(alias2)) => alias1.getName == alias2.getName
    case (Some(_), None)              => false
    case (None, Some(_))              => false
    case (None, None)                 => true
  }

  def selectExprEqual(sei1: SelectExpressionItem, sei2: SelectExpressionItem): Boolean = sei1.getExpression.toString equalsIgnoreCase sei2.getExpression.toString

  def tableNamesEqual(col1: AllTableColumns, col2: AllTableColumns): Boolean = col1.getTable.getFullyQualifiedName == col2.getTable.getFullyQualifiedName

  def canMatchOther(that: SelectColumnWrapper): Boolean = (this.col, that.col) match {
    case (_: AllColumns, _: AllColumns)                       => true
    case (at1: AllTableColumns, at2: AllTableColumns)         => tableNamesEqual(at1, at2)
    case (s1: SelectExpressionItem, s2: SelectExpressionItem) => selectExprEqual(s1, s2)
    case _                                                    => false
  }

  override def getColName: String = col match {
    case _: AllColumns             => "*"
    case at: AllTableColumns       => at toString
    case set: SelectExpressionItem => set.getExpression toString
  }

  override def getRest: String = col match {
    case (_: AllColumns) | (_: AllTableColumns) => ""
    case (set: SelectExpressionItem)            => Option(set.getAlias) map (_.toString) getOrElse ""
  }

  def matchOther(that: SelectColumnWrapper): MatchType = (this.col, that.col) match {
    case (_: AllColumns, _: AllColumns) | (_: AllTableColumns, _: AllTableColumns) => SUCCESSFUL_MATCH

    case (selExpr1: SelectExpressionItem, selExpr2: SelectExpressionItem) =>
      if (compareAliases(Option(selExpr1.getAlias), Option(selExpr2.getAlias))) SUCCESSFUL_MATCH else UNSUCCESSFUL_MATCH

    case _ => UNSUCCESSFUL_MATCH
  }

  override def toString: String = col toString

}