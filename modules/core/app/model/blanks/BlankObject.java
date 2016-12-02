package model.blanks;

import play.twirl.api.Html;

@FunctionalInterface
public interface BlankObject {

  public Html render();
  
}
