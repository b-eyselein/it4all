package model;

public class XMLError {
  
  // FIXME: better use -1, since maybe line numbers in xml documents are counted
  // starting from zero...
  protected int line = 0;
  protected String title = "";
  protected String errorMessage;
  
  protected XmlErrorType errorType;
  
  // FIXME: use IllegalArgumentException instead of NullPointerException in all
  // constuctors with message: has not to be declared to be thrown and fits use
  // case better (checking of Arguments!)
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
  
  @Override
  public boolean equals(Object obj) {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    XMLError other = (XMLError) obj;
    if(errorMessage == null) {
      if(other.errorMessage != null)
        return false;
    } else if(!errorMessage.equals(other.errorMessage))
      return false;
    return true;
  }
  
  public String getErrorMessage() {
    return errorMessage;
  }
  
  public XmlErrorType getErrorType() {
    return errorType;
  }
  
  public int getLine() {
    return line;
  }
  
  public String getTitle() {
    return title;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
    return result;
  }
  
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
  
  public void setErrorType(XmlErrorType errorType) {
    this.errorType = errorType;
  }
  
  public void setLine(int line) {
    this.line = line;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  @Override
  public String toString() {
    return (line != 0) ? errorType + ":\nZeile: " + line + "\nFehler: " + errorMessage + "\n"
        : errorType + ":\nFehler: " + errorMessage + "\n";
  }
}
