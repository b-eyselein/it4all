package model;

import java.util.List;

import model.Success;

public class ElementResult {

  protected Success success = Success.NONE;
  protected String title = "";
  protected String message = "";

  public ElementResult(Success theSuccess, String title, String message) {
    success = theSuccess;
	message = message;
	
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
