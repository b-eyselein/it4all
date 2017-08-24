package model.querycorrectors

import model.CorrectionException
import model.matching.Match
import model.matching.MatchType
import model.matching.MatchingResult
import net.sf.jsqlparser.expression.Alias
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.create.table.ColDataType
import net.sf.jsqlparser.statement.create.table.ColumnDefinition
import net.sf.jsqlparser.statement.select.AllColumns
import net.sf.jsqlparser.statement.select.AllTableColumns
import net.sf.jsqlparser.statement.select.SelectExpressionItem
import net.sf.jsqlparser.statement.select.SelectItem

abstract sealed class ColumnWrapper {

  type C

  def getColName: String

  def getRest: String

  def hasAlias: Boolean
  
  def canMatch(that: ColumnWrapper) = (this, that) match {
    case (arg1: SelectColumnWrapper, arg2: SelectColumnWrapper) => arg1.canMatchOther(arg2)
    case (arg1: ChangeColumnWrapper, arg2: ChangeColumnWrapper) => arg1.canMatchOther(arg2)
    case (arg1: CreateColumnWrapper, arg2: CreateColumnWrapper) => arg1.canMatchOther(arg2)
    case _ => throw new CorrectionException("", "Fehler bei Vergleich von zwei ColumnWrappers!")
  }

  def doMatch(that: ColumnWrapper): MatchType = (this, that) match {
    case (arg1: SelectColumnWrapper, arg2: SelectColumnWrapper) => arg1.matchOther(arg2)
    case (arg1: ChangeColumnWrapper, arg2: ChangeColumnWrapper) => arg1.matchOther(arg2)
    case (arg1: CreateColumnWrapper, arg2: CreateColumnWrapper) => arg1.matchOther(arg2)
    case _ => throw new CorrectionException("", "Fehler bei Matching von zwei ColumnWrappers!")
  }

}

object ColumnWrapper {
  def wrap(col: Column) = new ChangeColumnWrapper(col)
  def wrap(colDef: ColumnDefinition) = new CreateColumnWrapper(colDef)
  def wrap(selItem: SelectItem) = new SelectColumnWrapper(selItem)
}

case class ChangeColumnWrapper(col: Column) extends ColumnWrapper {

  override type C = Column

  // FIXME: eventually compare aliases?
  def canMatchOther(that: ChangeColumnWrapper): Boolean = getColName == that.getColName

  override def getColName = col.getColumnName

  override val getRest = ""
  
  override def hasAlias = false

  override def toString = col.toString

  def matchOther(that: ChangeColumnWrapper): MatchType = MatchType.SUCCESSFUL_MATCH
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
  def canMatchOther(that: CreateColumnWrapper) = getColName == that.getColName

  override def hasAlias = false
  
  override def getColName = col.getColumnName

  override def getRest = col.getColDataType.getDataType.toUpperCase

  def matchOther(that: CreateColumnWrapper) = {
    val theArg1 = col
    val theArg2 = that.col

    val datatypeName = col.getColumnName

    val typesOk = compareDataTypes(theArg1.getColDataType, theArg2.getColDataType)

    // FIXME: length of datatype? i. e. VARCHAR**(20)**

    if (typesOk) MatchType.SUCCESSFUL_MATCH else MatchType.UNSUCCESSFUL_MATCH
  }

  def compareDataTypes(userType: ColDataType, sampleType: ColDataType): Boolean = {
    val firstColType = userType.getDataType.toUpperCase
    val secondColType = sampleType.getDataType.toUpperCase

    // Comparing datatype
    firstColType.equalsIgnoreCase(secondColType)
    // return "Datentyp \"" + firstColType + "\" ist nicht korrekt, erwartet
    // wurde \"" + secondColType + "\"!"

    // TODO: Compare argumentslist?
    // List<String> userArgs = userType.getArgumentsStringList
    // List<String> sampleArgs = sampleType.getArgumentsStringList
    //
    // if(userArgs == null && sampleArgs == null)
    // return "Datentyp richtig spezifiziert"
    //
    // argumentsResult = Matcher.STRING_EQ_MATCHER.match("Argumente der
    // Datentyps", userArgs, sampleArgs)

    // return "Datentyp richtig spezifiziert"
  }

  override def toString = col.toString

}

case class SelectColumnWrapper(col: SelectItem) extends ColumnWrapper {

  override type C = SelectItem

  override def hasAlias = col.isInstanceOf[SelectExpressionItem] && col.asInstanceOf[SelectExpressionItem].getAlias != null
  
  def compareAliases(alias1: Alias, alias2: Alias) = alias1 != null && alias2 != null && alias1.getName == alias2.getName

  def selectExprEqual(sei1: SelectExpressionItem, sei2: SelectExpressionItem) = sei1.getExpression.toString.equalsIgnoreCase(sei2.getExpression.toString)

  def tableNamesEqual(col1: AllTableColumns, col2: AllTableColumns) = col1.getTable.getFullyQualifiedName == col2.getTable.getFullyQualifiedName

  def canMatchOther(that: SelectColumnWrapper) = (this.col, that.col) match {
    case (_: AllColumns, _: AllColumns) => true
    case (at1: AllTableColumns, at2: AllTableColumns) => tableNamesEqual(at1, at2)
    case (s1: SelectExpressionItem, s2: SelectExpressionItem) => selectExprEqual(s1, s2)
    case _ => false
  }

  override def getColName = col match {
    case _: AllColumns => "*"
    case at: AllTableColumns => at.toString
    case set: SelectExpressionItem => set.getExpression.toString
  }

  override def getRest = col match {
    case (_: AllColumns) | (_: AllTableColumns) => ""
    case (set: SelectExpressionItem) => if (set.getAlias == null) "" else set.getAlias.toString
  }

  def matchOther(that: SelectColumnWrapper) = (this.col, that.col) match {
    case (_: AllColumns, _: AllColumns) | (_: AllTableColumns, _: AllTableColumns) => MatchType.SUCCESSFUL_MATCH

    case (selExpr1: SelectExpressionItem, selExpr2: SelectExpressionItem) => {
      val aliasesCompared = selExpr1.getAlias != null || selExpr2.getAlias != null

      var aliasesEqual = false
      if (aliasesCompared)
        aliasesEqual = compareAliases(selExpr1.getAlias, selExpr2.getAlias)

      if (aliasesCompared && aliasesEqual) MatchType.SUCCESSFUL_MATCH else MatchType.UNSUCCESSFUL_MATCH
    }
    case _ => MatchType.UNSUCCESSFUL_MATCH

  }

  override def toString = col.toString

}