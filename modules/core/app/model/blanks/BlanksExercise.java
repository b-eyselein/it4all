package model.blanks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.exercise.Exercise;
import model.result.SuccessType;
import play.twirl.api.Html;

public class BlanksExercise extends Exercise {

  private final List<BlankObject> objects;

  public BlanksExercise(int id) {
    super(id);
    objects = Arrays.asList(new BlankObject(1,
        "&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;\n&lt;!DOCTYPE root SYSTEM \"doctype.dtd\"&gt;\n&lt;", 4,
        "&gt;", "root"), new BlankObject(2, "&lt;", 5, "&gt;", "/root"));
  }

  public List<SuccessType> correct(List<String> inputs) {
    if(inputs.size() != objects.size())
      throw new IllegalArgumentException("Es waren zu viele oder zu wenige Input-Elemente!");

    final List<SuccessType> results = new ArrayList<>(inputs.size());

    for(int i = 0; i < inputs.size(); i++)
      results.add(inputs.get(i).equals(objects.get(i).getSolution()) ? SuccessType.COMPLETE : SuccessType.NONE);

    return results;
  }

  public List<BlankObject> getObjects() {
    return objects;
  }

  public Html render() {
    return new Html(objects.stream().map(BlankObject::render).collect(Collectors.joining("\n")));
  }

}
