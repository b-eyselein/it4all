package controllers.programming;

import javax.inject.Inject;

import controllers.core.AExerciseAdminController;
import model.ProgExercise;
import model.ProgExerciseReader;
import model.user.User;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class ProgAdmin extends AExerciseAdminController<ProgExercise> {
  
  @Inject
  public ProgAdmin(FormFactory theFactory) {
    super(theFactory, ProgToolObject$.MODULE$, ProgExercise.finder, ProgExerciseReader.getInstance());
  }
  
  @Override
  public Result adminIndex() {
    return ok(views.html.progAdmin.index.render(getUser()));
  }
  
  @Override
  protected Html renderExEditForm(User user, ProgExercise exercise, boolean isCreation) {
    return views.html.progAdmin.editExForm.render(user, exercise);
  }
  
}
