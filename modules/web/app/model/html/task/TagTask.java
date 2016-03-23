package model.html.task;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.html.result.ElementResult;
import model.html.result.TagResult;

@Entity
@DiscriminatorValue("tag")
public class TagTask extends Task {

  @Override
  public ElementResult<? extends Task> getElementResult() {
    // TODO Auto-generated method stub
    return new TagResult(this);
  }

}
