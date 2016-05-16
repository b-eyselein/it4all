package model.exercise;

public abstract class EvaluationResult {
  
  protected Success success = Success.NONE;

  public int getPoints() {
    return success.getPoints();
  }

  public Success getSuccess() {
    return success;
  }

  public void setSuccess(Success suc) {
    if(suc == null)
      throw new IllegalArgumentException("Succes kann nicht auf \"null\" gesetzt werden!");
    success = suc;
  }

}
