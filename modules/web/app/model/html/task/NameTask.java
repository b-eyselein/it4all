package model.html.task;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.html.result.ElementResult;
import model.html.result.NameResult;

@Entity
@DiscriminatorValue("name")
public class NameTask extends Task {
  
  @Column(name = "elemName")
  public String elemName;
  
  @Override
  public ElementResult<? extends Task> getElementResult() {
    return new NameResult(this, tagName, elemName, attributes);
  }
  
}
