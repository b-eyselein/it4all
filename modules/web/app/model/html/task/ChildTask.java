package model.html.task;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import model.html.result.ChildResult;
import model.html.task.ChildTaskKey;
import model.html.task.Task;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "childtask")
public class ChildTask extends Model {

  @EmbeddedId
  public ChildTaskKey key;

  @ManyToOne
  @JoinColumns({@JoinColumn(name = "task_id", referencedColumnName = "id"),
    @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id")})
  @JsonBackReference
  public Task task;

  @Column(name = "tagName")
  public String tagName;

  public String attributes;

  public ChildResult getChildResult() {
    return new ChildResult(this);
  }

}
