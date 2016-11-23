package model;

public class BooleanParsingException extends Exception {

  private static final long serialVersionUID = -701957829362690590L;
  private final String formula;
  
  public BooleanParsingException(String msg, String theFormula) {
    super(msg);
    formula = theFormula;
  }
  
  public String getFormula() {
    return formula;
  }
  
}
