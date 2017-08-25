package model.feedback;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import io.ebean.Finder;
import io.ebean.Model;

@Entity
public class Feedback extends Model {
  
  public enum EvaluatedTool {
    HTML, CSS, JSWEB, JS, SQL;
  }
  
  public static final Finder<FeedbackKey, Feedback> finder = new Finder<>(Feedback.class);
  
  @EmbeddedId
  public FeedbackKey key;
  
  @Enumerated(EnumType.STRING)
  public Mark sense = Mark.NO_MARK;
  
  @Enumerated(EnumType.STRING)
  public Mark used = Mark.NO_MARK;
  
  @Enumerated(EnumType.STRING)
  public Mark usability = Mark.NO_MARK;
  
  @Enumerated(EnumType.STRING)
  public Mark feedback = Mark.NO_MARK;
  
  @Enumerated(EnumType.STRING)
  public Mark fairness = Mark.NO_MARK;
  
  @Column(columnDefinition = "text")
  public String comment = "";
  
  public Feedback(FeedbackKey theKey) {
    key = theKey;
  }
  
  public Mark get(EvaluatedAspect evaledAspect) {
    // FIXME: ugly hack...
    switch(evaledAspect) {
    case FAIRNESS_OF_FEEDBACK:
      return fairness;
    case SENSE:
      return sense;
    case STYLE_OF_FEEDBACK:
      return feedback;
    case USABILITY:
      return usability;
    case USED:
      return used;
    default:
      return Mark.NO_MARK;
    }
  }
  
  public void set(EvaluatedAspect evaledAspect, Mark mark) {
    switch(evaledAspect) {
    case FAIRNESS_OF_FEEDBACK:
       fairness = mark;
       break;
    case SENSE:
       sense = mark;
       break;
    case STYLE_OF_FEEDBACK:
       feedback = mark;
       break;
    case USABILITY:
       usability = mark;
       break;
    case USED:
       used = mark;
       break;
    default:
      break;
    }
  }
  
  public EvaluatedTool theTool() {
    return key.tool;
  }
  
}
