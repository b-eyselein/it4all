package model.html.task;

import javax.persistence.Column;
import javax.persistence.Entity;

import model.html.result.ElementResult;
import model.html.result.MultiNameResult;

@Entity
public class MultiNameTask extends Task {

  @Column(name = "elemName")
  public String elemName;

  @Override
  public ElementResult<? extends Task> getElementResult() {
    // TODO Auto-generated method stub
    return new MultiNameResult(this, tagName, elemName, attributes, attributes);
  }

}
