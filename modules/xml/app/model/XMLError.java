package model;

public class XMLError {
  
  protected int line;
  protected String errorMessage;
  protected XmlErrorType errorType;
  
  public XMLError(int line, String errorMessage, XmlErrorType errorType) throws NullPointerException {
    if(line <= 0 || errorMessage == null || errorType == null) {
      throw new NullPointerException();
    }
    this.line = line;
    this.errorMessage = errorMessage;
    this.errorType = errorType;
  }
  
  public XMLError(String errorMessage, XmlErrorType errorType) throws NullPointerException {
    if(errorMessage == null || errorType == null) {
      throw new NullPointerException();
    }
    this.errorMessage = errorMessage;
    this.errorType = errorType;
  }
  
  public int getLine() {
    return line;
  }
  
  public void setLine(int line) {
    this.line = line;
  }
  
  public String getErrorMessage() {
    return errorMessage;
  }
  
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
  
  public XmlErrorType getErrorType() {
    return errorType;
  }
  
  public void setErrorType(XmlErrorType errorType) {
    this.errorType = errorType;
  }
  
}
