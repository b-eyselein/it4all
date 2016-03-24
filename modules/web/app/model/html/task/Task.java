package model.html.task;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import model.html.HtmlExercise;
import model.html.result.ChildTask;
import model.html.result.ElementResult;

import com.avaje.ebean.Model;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Task extends Model {

  @EmbeddedId
  public TaskKey key;

  @ManyToOne
  @JoinColumn(name = "exercise_id", insertable = false, updatable = false)
  public HtmlExercise exercise;

  @Column(name = "taskDesc")
  public String taskDescription;

  @Column(name = "tagName")
  public String tagName;
  
  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  public List<ChildTask> childTasks;

  public String attributes;

  public abstract ElementResult<? extends Task> getElementResult();

}
