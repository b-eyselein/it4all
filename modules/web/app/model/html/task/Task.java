package model.html.task;

import org.openqa.selenium.SearchContext;

import model.html.result.ElementResult;

public interface Task {
  
  public static final String MULTIPLE_ATTRIBUTES_SPLIT_CHARACTER = ";";
  public static final String KEY_VALUE_CHARACTER = "=";

  public ElementResult evaluate(SearchContext context);

  public String getDescription();

  public int getId();

}
