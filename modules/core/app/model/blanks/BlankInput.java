package model.blanks;

import play.twirl.api.Html;

public class BlankInput implements BlankObject {

  private int length;

  public BlankInput(int theLength) {
    length = theLength;
  }

  @Override
  public Html render() {
    return new Html("<input type=\"text\" size=\"" + length + "\" + maxlength=\"" + length + "\">");
  }

}
