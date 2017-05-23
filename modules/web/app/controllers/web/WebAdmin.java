package controllers.web;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.WebExercise;
import model.WebExerciseReader;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class WebAdmin extends AbstractAdminController<WebExercise, WebExerciseReader> {

  @Inject
  public WebAdmin(FormFactory theFactory) {
    super(theFactory, WebExercise.finder, "web", new WebExerciseReader());
  }

  @Override
  public Result index() {
    return ok(views.html.webAdmin.index.render(getUser()));
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
  public Html renderCreated(List<WebExercise> created) {
    return views.html.webAdmin.webCreation.render(created);
  }

  @Override
  public Result uploadForm() {
    return ok(views.html.webAdmin.webupload.render(getUser()));
  }

}
