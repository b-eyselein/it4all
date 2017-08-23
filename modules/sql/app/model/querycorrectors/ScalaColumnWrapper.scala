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

abstract sealed class ScalaColumnWrapper {

  type C

  def canMatchOther(that: C): Boolean

  def getColName: String

  def getRest: String

  def wrap(col: Column) = new ChangeColumnWrapper(col)

  def wrap(colDef: ColumnDefinition) = new CreateColumnWrapper(colDef)

  def wrap(selItem: SelectItem) = new SelectColumnWrapper(selItem)

  def canMatch(that: ScalaColumnWrapper) = (this, that) match {
    case (arg1: SelectColumnWrapper, arg2: SelectColumnWrapper) => arg1.canMatchOther(arg2)

    case (arg1: ChangeColumnWrapper, arg2: ChangeColumnWrapper) => arg1.canMatchOther(arg2)

    case (arg1: CreateColumnWrapper, arg2: CreateColumnWrapper) => arg1.canMatchOther(arg2)

    case _ => throw new CorrectionException("", "Fehler bei Vergleich von zwei ColumnWrappers!")
  }

  //    def doMatch(ColumnWrapper other) throws CorrectionException {
  //      if(this instanceof SelectColumnWrapper && other instanceof SelectColumnWrapper)
  //        return ((SelectColumnWrapper) this).matchOther((SelectColumnWrapper) other)
  //  
  //      if(this instanceof ChangeColumnWrapper && other instanceof ChangeColumnWrapper)
  //        return ((ChangeColumnWrapper) this).matchOther((ChangeColumnWrapper) other)
  //  
  //      if(this instanceof CreateColumnWrapper && other instanceof CreateColumnWrapper)
  //        return ((CreateColumnWrapper) this).matchOther((CreateColumnWrapper) other)
  //  
  //      throw new CorrectionException("", "Fehler bei Matching von zwei ColumnWrappers!")
  //    }
}

case class ChangeColumnWrapper(col: Column) extends ScalaColumnWrapper {
  override type C = Column

  // FIXME: eventually compare aliases?
  def canMatchOther(other: ChangeColumnWrapper) = getColName == other.getColName

  override def getColName() = col.getColumnName

  override val getRest = ""

  def matchOther(other: ChangeColumnWrapper) = MatchType.SUCCESSFUL_MATCH
}

case class CreateColumnWrapper(col: ColumnDefinition) extends ScalaColumnWrapper {
  override type C = ColumnDefinition
  //
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
  //
  //    public CreateColumnWrapper(ColumnDefinition theColumnDefinition) {
  //      colDefinition = theColumnDefinition
  //    }

  // FIXME: eventually compare aliases?
  def canMatchOther(other: CreateColumnWrapper) = getColName == other.getColName

  override def getColName = col.getColumnName

  //    override
  //    public String getRest() {
  //      return colDefinition.getColDataType().getDataType().toUpperCase()
  //    }
  //
  //    public MatchType matchOther(CreateColumnWrapper other) {
  //      ColumnDefinition theArg1 = colDefinition
  //      ColumnDefinition theArg2 = other.colDefinition
  //
  //      datatypeName = colDefinition.getColumnName()
  //
  //      typesOk = compareDataTypes(theArg1.getColDataType(), theArg2.getColDataType())
  //
  //      // FIXME: length of datatype? i. e. VARCHAR**(20)**
  //
  //      return typesOk ? MatchType.SUCCESSFUL_MATCH : MatchType.UNSUCCESSFUL_MATCH
  //    }
  //
  //    private boolean compareDataTypes(ColDataType userType, ColDataType sampleType) {
  //      firstColType = userType.getDataType().toUpperCase()
  //      secondColType = sampleType.getDataType().toUpperCase()
  //
  //      // Comparing datatype
  //      return firstColType.equalsIgnoreCase(secondColType)
  //      // return "Datentyp \"" + firstColType + "\" ist nicht korrekt, erwartet
  //      // wurde \"" + secondColType + "\"!"
  //
  //      // TODO: Compare argumentslist?
  //      // List<String> userArgs = userType.getArgumentsStringList()
  //      // List<String> sampleArgs = sampleType.getArgumentsStringList()
  //      //
  //      // if(userArgs == null && sampleArgs == null)
  //      // return "Datentyp richtig spezifiziert"
  //      //
  //      // argumentsResult = Matcher.STRING_EQ_MATCHER.match("Argumente der
  //      // Datentyps", userArgs, sampleArgs)
  //
  //      // return "Datentyp richtig spezifiziert"
  //    }
}

case class SelectColumnWrapper(col: SelectItem) extends ScalaColumnWrapper {

  override type C = SelectItem

  //    public SelectColumnWrapper(SelectItem theSelectItem) {
  //      selectItem = theSelectItem
  //    }
  //
  //    private boolean compareAliases(Alias alias1, Alias alias2) {
  //      return alias1 != null && alias2 != null && alias1.getName().equals(alias2.getName())
  //    }
  //
  //    private boolean selectExprEqual(SelectExpressionItem selExprItem, SelectExpressionItem selExprItem2) {
  //      return selExprItem.getExpression().toString().equalsIgnoreCase(selExprItem2.getExpression().toString())
  //    }
  //
  //    private boolean tableNamesEqual(AllTableColumns col1, AllTableColumns col2) {
  //      return col1.getTable().getFullyQualifiedName().equals(col2.getTable().getFullyQualifiedName())
  //    }
  //
  def canMatchOther(other: SelectColumnWrapper) = {
    //      if(selectItem instanceof AllColumns)
    //        return other.selectItem instanceof AllColumns
    //
    //      if(selectItem instanceof AllTableColumns)
    //        return other.selectItem instanceof AllTableColumns
    //            && tableNamesEqual((AllTableColumns) selectItem, (AllTableColumns) other.selectItem)
    //
    //      if(selectItem instanceof SelectExpressionItem)
    //        return other.selectItem instanceof SelectExpressionItem
    //            && selectExprEqual((SelectExpressionItem) selectItem, (SelectExpressionItem) other.selectItem)
    false
  }
  //
  //    override
  //    public String getColName() {
  //      if(selectItem instanceof AllColumns)
  //        return "*"
  //
  //      if(selectItem instanceof AllTableColumns)
  //        return ((AllTableColumns) selectItem).toString()
  //
  //      return ((SelectExpressionItem) selectItem).getExpression().toString()
  //    }
  //
  //    override
  //    public String getRest() {
  //      if(selectItem instanceof AllColumns || selectItem instanceof AllTableColumns)
  //        return ""
  //
  //      Alias alias = ((SelectExpressionItem) selectItem).getAlias()
  //      return alias != null ? alias.toString() : ""
  //    }
  //
  //    public MatchType matchOther(SelectColumnWrapper other) {
  //      if((selectItem instanceof AllColumns && other.selectItem instanceof AllColumns)
  //          || (selectItem instanceof AllTableColumns && other.selectItem instanceof AllTableColumns))
  //        return MatchType.SUCCESSFUL_MATCH
  //
  //      if(!(selectItem instanceof SelectExpressionItem) || !(other.selectItem instanceof SelectExpressionItem))
  //        return MatchType.UNSUCCESSFUL_MATCH
  //
  //      SelectExpressionItem selExpr1 = (SelectExpressionItem) selectItem
  //      SelectExpressionItem selExpr2 = (SelectExpressionItem) other.selectItem
  //
  //      boolean aliasesCompared = selExpr1.getAlias() != null || selExpr2.getAlias() != null
  //
  //      boolean aliasesEqual = false
  //      if(aliasesCompared)
  //        aliasesEqual = compareAliases(selExpr1.getAlias(), selExpr2.getAlias())
  //
  //      return aliasesCompared && aliasesEqual ? MatchType.SUCCESSFUL_MATCH : MatchType.UNSUCCESSFUL_MATCH
  //    }
  //  }

}