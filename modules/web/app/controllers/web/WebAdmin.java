package controllers.web;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.StringConsts;
import model.WebExercise;
import model.WebExerciseReader;
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
  protected void initRemainingExFromForm(DynamicForm form, WebExercise exercise) {
    // TODO Auto-generated method stub

  }

}
