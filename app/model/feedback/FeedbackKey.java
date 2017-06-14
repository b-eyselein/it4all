package model.feedback;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import model.IntConsts;
import model.feedback.Feedback.EvaluatedTool;

@Embeddable
public class FeedbackKey implements Serializable {
  
  private static final long serialVersionUID = 4538444316510101194L;
  
  public String user;
  
  @Enumerated(EnumType.STRING)
  public EvaluatedTool tool;
  
  public FeedbackKey(String theUser, EvaluatedTool theTool) {
    user = theUser;
    tool = theTool;
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof FeedbackKey && hashCode() == obj.hashCode();
  }
  
  @Override
  public int hashCode() {
    return IntConsts.THOUSAND * user.hashCode() + tool.ordinal();
  }
  
}
