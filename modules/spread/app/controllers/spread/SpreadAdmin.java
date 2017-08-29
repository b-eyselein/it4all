package controllers.spread;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AExerciseAdminController;
import model.SpreadExercise;
import model.SpreadExerciseReader;
import model.user.User;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class SpreadAdmin extends AExerciseAdminController<SpreadExercise> {

  @Inject
  public SpreadAdmin(FormFactory theFactory) {
    super(theFactory, SpreadExercise.finder, SpreadExerciseReader.getInstance());
  }

  @Override
  public Result index() {
    return ok(views.html.spreadAdmin.index.render(getUser()));
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
  public Html renderCreated(List<SpreadExercise> created) {
    return views.html.spreadAdmin.spreadCreation.render(created);
  }

  @Override
  protected Html renderExercises(User user, List<SpreadExercise> allExercises) {
    // TODO Auto-generated method stub
    return null;
  }

}
