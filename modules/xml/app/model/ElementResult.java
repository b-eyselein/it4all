package model;

public class ElementResult {

  protected Success success = Success.NONE;
  protected String title = "";
  protected String message = "";

  public ElementResult(Success theSuccess, String title, String message) {
    this.success = theSuccess;
	this.title = title;
	this.message = message;
  }
  
  public Success getSuccess() {
    return success;
  }
  
  public String getTitle() {
    return title;
  }
  
  public String getMessage() {
    return message;
  }
}
