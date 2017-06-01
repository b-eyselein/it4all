package model.mindmap.evaluation;

public class ParsingException extends Exception {

  private static final long serialVersionUID = 71684384684L;

  public ParsingException(String msg) {
    super(msg);
  }

  public ParsingException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public ParsingException(Throwable cause) {
    super(cause);
  }

}
