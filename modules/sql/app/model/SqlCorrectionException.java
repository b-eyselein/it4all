package model;

public class SqlCorrectionException extends Exception {

  private static final long serialVersionUID = 3797086667659792252L;

  public SqlCorrectionException(String msg) {
    super(msg);
  }

  public SqlCorrectionException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public String getCauseMessage() {
    Throwable cause = getCause();
    while(cause != null) {
      if(cause.getMessage() != null)
        return cause.getMessage();
      cause = cause.getCause();
    }
    return null;
  }

}
