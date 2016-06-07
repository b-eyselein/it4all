package model.html.task;

import org.openqa.selenium.SearchContext;

import model.html.result.ElementResult;

public interface Task {
  
  public ElementResult evaluate(SearchContext context);

  public String getDescription();
  
  public int getId();
}
