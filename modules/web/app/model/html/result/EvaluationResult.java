package model.html.result;

import play.Logger;

public abstract class EvaluationResult {
  
  protected Success success = Success.NONE;

  public int getPoints() {
    if(success == Success.NONE)
      return 0;
    if(success == Success.PARTIALLY)
      return 1;
    if(success == Success.COMPLETE)
      return 2;
    Logger.error("Wrong type of Success!");
    return -1;
  }

  public Success getSuccess() {
    return success;
  }

  public void setSuccess(Success suc) {
    success = suc;
  }

}
