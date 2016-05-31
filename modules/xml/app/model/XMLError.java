package model;

public class XMLError {
  
  protected int line;
  protected String title;
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
  
  public XMLError(XmlErrorType errorType, String title, String errorMessage) {
    if(errorMessage == null || errorType == null) {
      throw new NullPointerException();
    }
    this.errorMessage = errorMessage;
    this.errorType = errorType;
    this.title = title;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  
}