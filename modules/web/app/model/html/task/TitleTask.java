package model.html.task;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.html.result.ElementResult;
import model.html.result.TitleResult;

@Entity
@DiscriminatorValue("title")
public class TitleTask extends Task {

  public String title;

  @Override
  public ElementResult<? extends Task> getElementResult() {
    return new TitleResult(this, title);
  }
  
}
