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
  public YesNoMaybe sense = YesNoMaybe.MAYBE;
  
  @Enumerated(EnumType.STRING)
  public YesNoMaybe used = YesNoMaybe.MAYBE;
  
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
  
  public EvaluatedTool theTool() {
    return key.tool;
  }
  
}
