package model.task;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.ebean.Finder;

@Entity
public class JsWebTask extends WebTask {

  public static final Finder<WebTaskKey, JsWebTask> finder = new Finder<>(JsWebTask.class);

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  @JsonManagedReference
  public List<Condition> conditions;

  @Embedded
  public Action action;

  public JsWebTask(WebTaskKey theKey) {
    super(theKey);
  }

  public void saveInDB() {
    save();
    conditions.forEach(Condition::save);
  }

}
