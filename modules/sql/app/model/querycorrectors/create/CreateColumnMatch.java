package model.querycorrectors.create;

import model.matching.Match;
import model.matching.MatchType;
import model.matching.MatchingResult;
import model.querycorrectors.ColumnMatch;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class CreateColumnMatch extends ColumnMatch<ColumnDefinition> {

  private String datatypeName;

  private String message;
  
  private String firstColType;
  
  private String secondColType;

  private MatchingResult<String, Match<String>> argumentsResult;
  
  private boolean typesOk;

  public CreateColumnMatch(ColumnDefinition theArg1, ColumnDefinition theArg2) {
    super(theArg1, theArg2);
  }

  public String getDatatypeName() {
    return datatypeName;
  }

  @Override
  public String getFirstColName() {
    return userArg != null ? userArg.getColumnName() : "--";
  }

  @Override
  public String getFirstRest() {
    return firstColType;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String getSecondColName() {
    return sampleArg != null ? sampleArg.getColumnName() : "--";
  }

  @Override
  public String getSecondRest() {
    return secondColType;
  }

  @Override
  public boolean hasAlias() {
    return false;
  }

  @Override
  public boolean restMatched() {
    // TODO Auto-generated method stub
    return typesOk;
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

  @Override
  protected MatchType analyze(ColumnDefinition theArg1, ColumnDefinition theArg2) {
    datatypeName = theArg1.getColumnName();

    typesOk = compareDataTypes(theArg1.getColDataType(), theArg2.getColDataType());

    // FIXME: length of datatype? i. e. VARCHAR**(20)**

    return typesOk ? MatchType.SUCCESSFUL_MATCH : MatchType.UNSUCCESSFUL_MATCH;
  }

}
