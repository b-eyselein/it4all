package model.feedback;

public enum Mark {
  
  // @formatter:off
  VERY_GOOD     (1),
  GOOD          (2),
  NEUTRAL       (3),
  BAD           (4),
  VERY_BAD      (5),
  
  NO_MARK       (-1);
  // @formatter:on
  
  private int value;
  
  private Mark(int theMark) {
    value = theMark;
  }
  
  public int getMark() {
    return value;
  }
  
  public String display(EvaluatedAspect evaledAspect) {
    switch(this) {
    case VERY_GOOD:
      return "Sehr " + evaledAspect.getPositive().toLowerCase();
    case GOOD:
      return evaledAspect.getPositive();
    case NEUTRAL:
      return evaledAspect.getNeutral();
    case BAD:
      return evaledAspect.getNegative();
    case VERY_BAD:
      return "Sehr " + evaledAspect.getNegative().toLowerCase();
    case NO_MARK:
    default:
      return "Keine Angabe";
    }
  }
  
}
