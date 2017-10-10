package controllers.programming;

import controllers.core.AExerciseAdminController;
import model.ProgExercise;
import model.ProgExerciseReader;
import model.user.User;
import play.api.Configuration;
import play.data.FormFactory;
import play.twirl.api.Html;

import javax.inject.Inject;

public class ProgAdmin extends AExerciseAdminController<ProgExercise> {

  @Inject public ProgAdmin(Configuration c, FormFactory f) {
    super(c, f, new ProgToolObject(c), ProgExercise.finder, ProgExerciseReader.getInstance());
  }

  @Override public Html renderAdminIndex(User user) {
    return views.html.progAdmin.index.render(user);
  }

  @Override public Html renderExEditForm(User user, ProgExercise exercise, boolean isCreation) {
    return views.html.progAdmin.editExForm.render(user, exercise);
  }

}
