package controllers.spread;

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
    super(theFactory, SpreadToolObject$.MODULE$, SpreadExercise.finder, SpreadExerciseReader.getInstance());
  }
  
  @Override
  public Result adminIndex() {
    return ok(views.html.spreadAdmin.index.render(getUser()));
  }
  
  @Override
  protected Html renderExEditForm(User user, SpreadExercise exercise, boolean isCreation) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
