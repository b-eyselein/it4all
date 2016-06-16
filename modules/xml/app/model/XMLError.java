package model;

public class XMLError {
  
  protected int line = -1;
  protected String title = "";
  protected String errorMessage;
  
  protected XmlErrorType errorType;
  
  public XMLError(int line, String errorMessage, XmlErrorType errorType) {
    if(line < 0) {
      throw new IllegalArgumentException("Linenumber has to be greater equal 0");
    }
    if(errorMessage == null || errorType == null) {
      throw new IllegalArgumentException("An XMLError has a message and a type");
    }
    this.line = line;
    this.errorMessage = errorMessage;
    this.errorType = errorType;
  }
  
  public XMLError(String errorMessage, XmlErrorType errorType) {
    if(errorMessage == null || errorType == null) {
      throw new IllegalArgumentException("An XMLError has a message and a type");
    }
    this.errorMessage = errorMessage;
    this.errorType = errorType;
  }
  
  public XMLError(String title, String errorMessage, XmlErrorType errorType) {
    if(title == null) {
      throw new IllegalArgumentException("Title can not be null");
    }
    if(errorMessage == null || errorType == null) {
      throw new IllegalArgumentException("An XMLError has a message and a type");
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
    if(errorType == XmlErrorType.NONE)
      return errorType.toString();
    else
      return (line != -1) ? errorType + ":\nZeile: " + line + "\nFehler: " + errorMessage + "\n"
          : errorType + ":\nFehler: " + errorMessage + "\n";
  }
}
