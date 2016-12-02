package model.blanks;

import play.twirl.api.Html;

public class BlankText implements BlankObject {
  
  private String text;

  public BlankText(String theText) {
    text = theText;
  }

  @Override
  public Html render() {
    return new Html(text);
  }
  
}
