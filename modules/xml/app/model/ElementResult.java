package model;

public class ElementResult {

  protected XmlErrorType success = XmlErrorType.NONE;
  protected String title = "";
  protected String message = "";

  public ElementResult(XmlErrorType theSuccess, String title, String message) {
    this.success = theSuccess;
	this.title = title;
	this.message = message;
  }
  
  public XmlErrorType getSuccess() {
    return success;
  }
  
  public String getTitle() {
    return title;
  }
  
  public String getMessage() {
    return message;
  }
}
