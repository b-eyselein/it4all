package controllers.spread;

import javax.inject.Inject;

import controllers.core.AExerciseAdminController;
import model.SpreadExercise;
import model.SpreadExerciseReader;
import model.user.User;
import play.data.FormFactory;
import play.twirl.api.Html;

public class SpreadAdmin extends AExerciseAdminController<SpreadExercise> {
  
  @Inject
  public SpreadAdmin(FormFactory theFactory) {
    super(theFactory, SpreadToolObject$.MODULE$, SpreadExercise.finder, SpreadExerciseReader.getInstance());
  }
  
  @Override
  public Html renderAdminIndex(User user) {
    return views.html.spreadAdmin.index.render(user);
  }
  
  @Override
  public Html renderExEditForm(User user, SpreadExercise exercise, boolean isCreation) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
