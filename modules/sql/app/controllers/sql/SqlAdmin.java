package controllers.sql;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.SqlExerciseReader;
import model.StringConsts;
import model.exercise.SqlScenario;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class SqlAdmin extends AbstractAdminController<SqlScenario, SqlExerciseReader> {

  @Inject
  public SqlAdmin(FormFactory theFactory) {
    super(theFactory, SqlScenario.finder, "sql", new SqlExerciseReader());
  }

  @Override
  public SqlScenario getNew(int id) {
    return new SqlScenario(id);
  }

  @Override
  public Result index() {
    return ok(views.html.sqlAdmin.index.render(getUser()));
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

  public Result newScenario() {
    DynamicForm form = factory.form().bindFromRequest();
    SqlScenario scenario = new SqlScenario(findMinimalNotUsedId(finder));

    scenario.author = form.get(StringConsts.AUTHOR_NAME);
    scenario.scriptFile = form.get("scriptFile");
    scenario.shortName = form.get("shortName");
    scenario.text = form.get(StringConsts.TEXT_NAME);
    scenario.title = form.get(StringConsts.TITLE_NAME);

    // FIXME: how to save new sql scenario?!
    // scenario.saveInDB();

    return ok(views.html.preview.render(getUser(), views.html.sqlAdmin.sqlCreation.render(scenario)));
  }

  public Result newScenarioForm() {
    return ok(views.html.sqlAdmin.newScenarioForm.render(getUser()));
  }

  @Override
  public Html renderCreated(List<SqlScenario> created) {
    // Guaranteed to be always one scenario by json Schema!
    return views.html.sqlAdmin.sqlCreation.render(created.get(0));
  }

  public Result scenarioAdmin(int id) {
    return ok(views.html.sqlAdmin.scenarioAdmin.render(getUser(), SqlScenario.finder.byId(id)));
  }

  @Override
  protected void initRemainingExFromForm(DynamicForm form, SqlScenario exercise) {
    // TODO Auto-generated method stub

  }

}
