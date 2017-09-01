package controllers.web;

import javax.inject.Inject;

import controllers.core.AExerciseAdminController;
import model.WebExercise;
import model.WebExerciseReader;
import model.user.User;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class WebAdmin extends AExerciseAdminController<WebExercise> {

  @Inject
  public WebAdmin(FormFactory theFactory) {
    super(theFactory, WebRoutesObject$.MODULE$, WebExercise.finder, WebExerciseReader.getInstance());
  }

  @Override
  public Result index() {
    return ok(views.html.webAdmin.index.render(getUser()));
  }

  @Override
  protected Html renderExEditForm(User user, WebExercise exercise, boolean isCreation) {
    return views.html.webAdmin.editExForm.render(user, exercise);
  }

  // @Override
  // protected Html renderExercises(List<WebExercise> exercises) {
  // return views.html.webAdmin.webCreation.render(exercises);
  // }

}
