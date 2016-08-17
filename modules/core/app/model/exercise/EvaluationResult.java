package model.exercise;

public abstract class EvaluationResult {

  protected Success success = Success.NONE;

  public EvaluationResult(Success theSuccess) {
    success = theSuccess;
  }

  public String getBSClass() {
    switch(success) {
    case COMPLETE:
      return "success";
    case PARTIALLY:
      return "warning";
    case NONE:
      return "danger";
    default:
      return "info";
    }
  }

  public int getPoints() {
    return success.getPoints();
  }

  public Success getSuccess() {
    return success;
  }

  public void setSuccess(Success suc) {
    if(suc == null)
      throw new IllegalArgumentException("Success kann nicht auf \"null\" gesetzt werden!");
    success = suc;
  }

}
