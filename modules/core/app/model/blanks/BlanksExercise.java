package model.blanks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.exercise.Exercise;
import play.Logger;
import play.twirl.api.Html;

public class BlanksExercise implements Exercise {

  // @formatter:off
  private List<BlankObject> objects = Arrays.asList(
      new BlankObject("&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;\n&lt;!DOCTYPE root SYSTEM \"doctype.dtd\"&gt;\n&lt;", 4, "&gt;", "root"),
      new BlankObject("&lt;", 5, "&gt;", "/root"));
  // @formatter:on

  public void correct(List<String> inputs) {
    // TODO Auto-generated method stub
    if(inputs.size() != objects.size())
      throw new IllegalArgumentException("Es waren zu viele oder zu wenige Input-Elemente!");

    for(int i = 0; i < inputs.size(); i++) {
      Logger.debug("Lernerlösung: >>" + inputs.get(i) + "<< :: Erwartet: >>" + objects.get(i).getSolution() + "<<");
    }
  }

  @Override
  public int getId() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getMaxPoints() {
    // TODO Auto-generated method stub
    return 0;
  }

  public List<BlankObject> getObjects() {
    return objects;
  }

  @Override
  public String getText() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getTitle() {
    // TODO Auto-generated method stub
    return null;
  }

  public Html render() {
    return new Html(objects.stream().map(BlankObject::render).collect(Collectors.joining("\n")));
  }

}
