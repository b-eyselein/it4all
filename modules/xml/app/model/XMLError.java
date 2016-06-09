package model;

public class XMLError {
  
  protected int line = 0;
  protected String title;
  protected String errorMessage;
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
    return result;
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
  
  @Override
  public String toString() {
    return (line != 0) ? errorType + ":\nZeile: " + line + "\nFehler: " + errorMessage + "\n"
        : errorType + ":\nFehler: " + errorMessage + "\n";
  }
}
