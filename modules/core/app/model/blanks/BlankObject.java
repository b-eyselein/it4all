package model.blanks;

public class BlankObject {

  private String preText;
  private int length;
  private String solution;
  private String postText;

  public BlankObject(String thePreText, int theLength, String thePostText, String theSolution) {
    preText = thePreText;
    length = theLength;
    postText = thePostText;
    solution = theSolution;
  }

  public String getPreText() {
    return preText;
  }

  public String getSolution() {
    return solution;
  }

  public String render() {
    return preText + "<div class=\"form-group\"><input class=\"form-control\" type=\"text\" size=\"" + length
        + "\" + maxlength=\"" + length + "\"></div>" + postText;
  }
}
