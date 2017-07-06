package controllers.web;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.WebExercise;
import model.WebExerciseReader;
import play.api.mvc.Call;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class WebAdmin extends AbstractAdminController<WebExercise, WebExerciseReader> {

  @Inject
  public WebAdmin(FormFactory theFactory) {
    super(theFactory, WebExercise.finder, "web", new WebExerciseReader());
  }

  @Override
  public WebExercise getNew(int id) {
    return new WebExercise(id);
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
  protected Call getIndex() {
    return controllers.web.routes.WebAdmin.index();
  }

  @Override
  protected void initRemainingExFromForm(DynamicForm form, WebExercise exercise) {
    // TODO Auto-generated method stub

  }

}
