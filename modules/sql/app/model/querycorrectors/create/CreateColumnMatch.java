package model.querycorrectors.create;

import java.util.List;

import model.matching.Match;
import model.matching.Matcher;
import model.matching.MatchingResult;
import model.querycorrectors.columnMatch.ColumnMatch;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class CreateColumnMatch extends ColumnMatch<ColumnDefinition> {
  
  private String datatypeName;
  
  private String message;
  
  private MatchingResult<String, Match<String>> argumentsResult;
  
  public CreateColumnMatch(ColumnDefinition theArg1, ColumnDefinition theArg2) {
    super(theArg1, theArg2);
  }
  
  @Override
  public String describe() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public String getDatatypeName() {
    return datatypeName;
  }
  
  public String getMessage() {
    return message;
  }
  
  private String compareDataTypes(ColDataType userType, ColDataType sampleType) {
    String userDataType = userType.getDataType().toUpperCase();
    String sampleDataType = sampleType.getDataType().toUpperCase();
    
    // Comparing datatype
    if(!userDataType.equals(sampleDataType))
      return "Datentyp \"" + userDataType + "\" ist nicht korrekt, erwartet wurde \"" + sampleDataType + "\"!";
    
    // TODO: Compare argumentslist?
    List<String> userArgs = userType.getArgumentsStringList();
    List<String> sampleArgs = sampleType.getArgumentsStringList();
    
    if(userArgs == null && sampleArgs == null)
      return "Datentyp richtig spezifiziert";
    
    argumentsResult = Matcher.STRING_EQ_MATCHER.match("Argumente der Datentyps", userArgs, sampleArgs);
    
    return "Datentyp richtig spezifiziert";
  }
  
  @Override
  protected boolean analyze(ColumnDefinition theArg1, ColumnDefinition theArg2) {
    datatypeName = theArg1.getColumnName();
    
    message = compareDataTypes(theArg1.getColDataType(), theArg2.getColDataType());
    
    // TODO: compare columnspecstrings!
    // Logger.debug("ColumnSpecString for column " + userDef.getColumnName()
    // + "
    // :: " + userDef.getColumnSpecStrings());
    return false;
  }
  
}
