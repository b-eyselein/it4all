package model.exercise;

public abstract class CreationResult<T> {
  
  protected CreationResultType resultType;
  
  protected T created;
  
  protected String message;
  
  public CreationResult(CreationResultType theResultType, T theCreated, String theMessage) {
    resultType = theResultType;
    created = theCreated;
    message = theMessage;
  }
  
  public abstract String getAsHtml();
  
  public T getCreated() {
    return created;
  }
  
  public String getMessage() {
    return message;
  }
  
  public CreationResultType getResultType() {
    return resultType;
  }
  
  protected String getBSClass() {
    switch(resultType) {
    case FAILURE:
      return "danger";
    case NOT_UPDATED:
      return "info";
    case TO_UPDATE:
      return "warning";
    case NEW:
      return "success";
    default:
      return "default";
    }
  }
  
}
