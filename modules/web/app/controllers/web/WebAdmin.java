package controllers.web;

import java.util.List;

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
    super(theFactory, WebExercise.finder, WebExerciseReader.getInstance());
  }
  
  @Override
  public Result index() {
    return ok(views.html.webAdmin.index.render(getUser()));
  }
  
  @Override
  public Result newExerciseForm() {
    return ok(views.html.webAdmin.newExerciseForm.render(getUser()));
  }
  
  @Override
  public Html renderCreated(List<WebExercise> created) {
    return views.html.webAdmin.webCreation.render(created);
  }
  
  @Override
  protected Html renderExercises(User user, List<WebExercise> allExercises) {
    return views.html.webAdmin.webExercises.render(user, allExercises);
  }
  
}
