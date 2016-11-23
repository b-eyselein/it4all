package model.feedback;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import model.feedback.Feedback.EvaluatedTool;

@Embeddable
public class FeedbackKey implements Serializable {
  
  private static final long serialVersionUID = 4538444316510101194L;
  
  public String user; // NOSONAR
  
  @Enumerated(EnumType.STRING)
  public EvaluatedTool tool; // NOSONAR
  
  public FeedbackKey(String theUser, EvaluatedTool theTool) {
    user = theUser;
    tool = theTool;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj == null || !(obj instanceof FeedbackKey))
      return false;
    FeedbackKey other = (FeedbackKey) obj;
    return user.equals(other.user) && tool == other.tool;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((tool == null) ? 0 : tool.hashCode());
    result = prime * result + ((user == null) ? 0 : user.hashCode());
    return result;
  }
  
}
