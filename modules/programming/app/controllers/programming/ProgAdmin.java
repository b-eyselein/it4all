package controllers.programming;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AExerciseAdminController;
import model.ProgExercise;
import model.ProgExerciseReader;
import model.ProgSample;
import model.testdata.SampleTestData;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class ProgAdmin extends AExerciseAdminController<ProgExercise> {

  @Inject
  public ProgAdmin(FormFactory theFactory) {
    super(theFactory, ProgExercise.finder, ProgExerciseReader.getInstance());
  }

  @Override
  public Result index() {
    return ok(views.html.progAdmin.index.render(getUser()));
  }

  @Override
  public Result newExercise() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result newExerciseForm() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Html renderCreated(List<ProgExercise> exercises) {
    return views.html.progAdmin.progCreation.render(exercises);
  }
  
  @Override
  protected ProgExercise initRemainingExFromForm(int id, String title, String author, String text, DynamicForm form) {
    String functionName = form.get("functionName");
    int inputCount = Integer.parseInt(form.get("inputCount"));

    List<ProgSample> samples = Collections.emptyList();
    List<SampleTestData> sampleTestData = Collections.emptyList();
    
    return new ProgExercise(id, title, author, text, functionName, inputCount, samples, sampleTestData);
  }

}
