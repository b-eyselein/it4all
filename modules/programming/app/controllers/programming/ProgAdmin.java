package controllers.programming;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.ProgExercise;
import model.ProgExerciseReader;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class ProgAdmin extends AbstractAdminController<ProgExercise, ProgExerciseReader> {
  
  @Inject
  public ProgAdmin(FormFactory theFactory) {
    super(theFactory, ProgExercise.finder, "prog", new ProgExerciseReader());
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
  public Result uploadForm() {
    // TODO Auto-generated method stub
    return ok("TODO");
  }
  
}
