package model.blanks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.exercise.Exercise;
import model.exercise.Success;
import play.twirl.api.Html;

public class BlanksExercise implements Exercise {

  private List<BlankObject> objects = Arrays.asList(new BlankObject(1,
      "&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;\n&lt;!DOCTYPE root SYSTEM \"doctype.dtd\"&gt;\n&lt;", 4, "&gt;",
      "root"), new BlankObject(2, "&lt;", 5, "&gt;", "/root"));

  public List<Success> correct(List<String> inputs) {
    if(inputs.size() != objects.size())
      throw new IllegalArgumentException("Es waren zu viele oder zu wenige Input-Elemente!");

    List<Success> results = new ArrayList<>(inputs.size());

    for(int i = 0; i < inputs.size(); i++)
      results.add(inputs.get(i).equals(objects.get(i).getSolution()) ? Success.COMPLETE : Success.NONE);

    return results;
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
