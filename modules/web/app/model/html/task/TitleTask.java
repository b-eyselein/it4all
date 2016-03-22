package model.html.task;

import javax.persistence.Entity;

import model.html.result.ElementResult;
import model.html.result.TitleResult;

@Entity
public class TitleTask extends Task {

  public String title;

  @Override
  public ElementResult getElementResult() {
    // TODO Auto-generated method stub
    return new TitleResult(this, title, attributes);
  }
  
}
