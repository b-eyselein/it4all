package model.querycorrectors;

import model.matching.Match;
import model.matching.MatchType;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

public abstract class ColumnWrapper {
  
  private static class ChangeColumnWrapper extends ColumnWrapper {
    private Column column;
    
    public ChangeColumnWrapper(Column theColumn) {
      column = theColumn;
    }
    
    public boolean canMatchOther(ChangeColumnWrapper other) {
      // FIXME: eventually compare aliases?
      return getColName().equals(other.getColName());
    }
    
    @Override
    public String getColName() {
      return column.getColumnName();
    }
    
    @Override
    public String getRest() {
      return "";
    }
    
    @Override
    public MatchType match(ColumnWrapper other) {
      return MatchType.SUCCESSFUL_MATCH;
      
    }
    
  }
  
  private static class CreateColumnWrapper extends ColumnWrapper {
    private ColumnDefinition columnDefinition;
    
    private String datatypeName;
    
    private String message;
    
    private String firstColType;
    
    private String secondColType;
    
    private MatchingResult<String, Match<String>> argumentsResult;
    
    private boolean typesOk;
    
    public CreateColumnWrapper(ColumnDefinition theColumnDefinition) {
      columnDefinition = theColumnDefinition;
    }
    
    public boolean canMatchOther(CreateColumnWrapper other) {
      // FIXME: eventually compare aliases?
      return getColName().equals(other.getColName());
    }
    
    @Override
    public String getColName() {
      return columnDefinition.getColumnName();
    }
    
    @Override
    public String getRest() {
      return columnDefinition.getColDataType().getDataType().toUpperCase();
    }
    
    @Override
    public MatchType match(ColumnWrapper other) {
      ColumnDefinition theArg1 = columnDefinition;
      ColumnDefinition theArg2 = ((CreateColumnWrapper) other).columnDefinition;

      datatypeName = columnDefinition.getColumnName();
      
      typesOk = compareDataTypes(theArg1.getColDataType(), theArg2.getColDataType());
      
      // FIXME: length of datatype? i. e. VARCHAR**(20)**
      
      return typesOk ? MatchType.SUCCESSFUL_MATCH : MatchType.UNSUCCESSFUL_MATCH;
    }
    
    private boolean compareDataTypes(ColDataType userType, ColDataType sampleType) {
      firstColType = userType.getDataType().toUpperCase();
      secondColType = sampleType.getDataType().toUpperCase();
      
      // Comparing datatype
      return firstColType.equalsIgnoreCase(secondColType);
      // return "Datentyp \"" + firstColType + "\" ist nicht korrekt, erwartet
      // wurde \"" + secondColType + "\"!";
      
      // TODO: Compare argumentslist?
      // List<String> userArgs = userType.getArgumentsStringList();
      // List<String> sampleArgs = sampleType.getArgumentsStringList();
      //
      // if(userArgs == null && sampleArgs == null)
      // return "Datentyp richtig spezifiziert";
      //
      // argumentsResult = Matcher.STRING_EQ_MATCHER.match("Argumente der
      // Datentyps", userArgs, sampleArgs);
      
      // return "Datentyp richtig spezifiziert";
    }
    
  }
  
  private static class SelectColumnWrapper extends ColumnWrapper {
    
    private SelectItem selectItem;
    
    public SelectColumnWrapper(SelectItem theSelectItem) {
      selectItem = theSelectItem;
    }
    
    private static boolean compareAliases(Alias alias1, Alias alias2) {
      return alias1 != null && alias2 != null && alias1.getName().equals(alias2.getName());
    }
    
    private static boolean selectExprEqual(SelectExpressionItem selExprItem, SelectExpressionItem selExprItem2) {
      return selExprItem.getExpression().toString().equalsIgnoreCase(selExprItem2.getExpression().toString());
    }
    
    private static boolean tableNamesEqual(AllTableColumns col1, AllTableColumns col2) {
      return col1.getTable().getFullyQualifiedName().equals(col2.getTable().getFullyQualifiedName());
    }
    
    public boolean canMatchOther(SelectColumnWrapper other) {
      SelectItem selItem1 = selectItem;
      SelectItem selItem2 = other.selectItem;
      if(selItem1 instanceof AllColumns) {
        return selItem2 instanceof AllColumns;
        
      } else if(selItem1 instanceof AllTableColumns) {
        return selItem2 instanceof AllTableColumns
            && tableNamesEqual((AllTableColumns) selItem1, (AllTableColumns) selItem2);
        
      } else if(selItem1 instanceof SelectExpressionItem) {
        return selItem2 instanceof SelectExpressionItem
            && selectExprEqual((SelectExpressionItem) selItem1, (SelectExpressionItem) selItem2);
      } else {
        return false;
      }
    }
    
    @Override
    public String getColName() {
      if(selectItem instanceof AllColumns)
        return "*";
      
      if(selectItem instanceof AllTableColumns)
        return ((AllTableColumns) selectItem).toString();
      
      return ((SelectExpressionItem) selectItem).getExpression().toString();
    }
    
    @Override
    public String getRest() {
      if(selectItem instanceof AllColumns || selectItem instanceof AllTableColumns)
        return "";
      
      Alias alias = ((SelectExpressionItem) selectItem).getAlias();
      return alias != null ? alias.toString() : "";
    }
    
    @Override
    public MatchType match(ColumnWrapper other) {
      SelectItem theArg1 = selectItem;
      SelectItem theArg2 = ((SelectColumnWrapper) other).selectItem;
      
      if((theArg1 instanceof AllColumns && theArg2 instanceof AllColumns)
          || (theArg1 instanceof AllTableColumns && theArg2 instanceof AllTableColumns))
        return MatchType.SUCCESSFUL_MATCH;
      
      if(!(theArg1 instanceof SelectExpressionItem) || !(theArg2 instanceof SelectExpressionItem))
        return MatchType.UNSUCCESSFUL_MATCH;
      
      SelectExpressionItem selExpr1 = (SelectExpressionItem) theArg1;
      SelectExpressionItem selExpr2 = (SelectExpressionItem) theArg2;
      
      boolean aliasesCompared = selExpr1.getAlias() != null || selExpr2.getAlias() != null;
      
      boolean aliasesEqual = false;
      if(aliasesCompared)
        aliasesEqual = compareAliases(selExpr1.getAlias(), selExpr2.getAlias());
      
      return aliasesCompared && aliasesEqual ? MatchType.SUCCESSFUL_MATCH : MatchType.UNSUCCESSFUL_MATCH;
    }
  }
  
  public static ColumnWrapper wrap(Column theColumn) {
    return new ChangeColumnWrapper(theColumn);
  }
  
  public static ColumnWrapper wrap(ColumnDefinition theColumnDefinition) {
    return new CreateColumnWrapper(theColumnDefinition);
  }
  
  public static ColumnWrapper wrap(SelectItem theSelectItem) {
    return new SelectColumnWrapper(theSelectItem);
  }
  
  public boolean canMatchOther(ColumnWrapper other) {
    if(this instanceof SelectColumnWrapper && other instanceof SelectColumnWrapper)
      return ((SelectColumnWrapper) this).canMatchOther((SelectColumnWrapper) other);
    
    if(this instanceof ChangeColumnWrapper && other instanceof ChangeColumnWrapper)
      return ((ChangeColumnWrapper) this).canMatchOther((ChangeColumnWrapper) other);
    
    if(this instanceof CreateColumnWrapper && other instanceof CreateColumnWrapper)
      return ((CreateColumnWrapper) this).canMatchOther((CreateColumnWrapper) other);
    
    return false;
  }
  
  public abstract String getColName();
  
  public abstract String getRest();
  
  public abstract MatchType match(ColumnWrapper other);
  
}
