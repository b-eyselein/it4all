package model.html.task;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import model.html.result.ChildResult;
import model.html.task.HtmlTask;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "childtask")
public class ChildTask extends Model {

  @Embeddable
  public static class ChildTaskKey implements Serializable {

    private static final long serialVersionUID = 7072289741394461703L;
    
    public int id; // NOSONAR
    
    public int taskId; // NOSONAR
    
    @Override
    public boolean equals(Object other) {
      if(other == null || !(other instanceof ChildTaskKey))
        return false;

      ChildTaskKey otherKey = (ChildTaskKey) other;
      return otherKey.id == id && otherKey.taskId == taskId;
    }
    
    @Override
    public int hashCode() {
      return 1000 * taskId + id;
    }
  }
  
  @EmbeddedId
  public ChildTaskKey key;
  
  @ManyToOne
  // @formatter:off
  @JoinColumns({
      @JoinColumn(name = "task_id", referencedColumnName = "task_id"),
      @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id")
  })
  //@formatter:on
  @JsonBackReference
  public HtmlTask task;
  
  @Column(name = "tagName")
  public String tagName;
  
  public String definingAttribute; // NOSONAR
  
  public ChildResult getChildResult() {
    return new ChildResult(this);
  }
}
