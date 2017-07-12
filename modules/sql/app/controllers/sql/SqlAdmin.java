package controllers.sql;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.SqlScenarioReader;
import model.StringConsts;
import model.exercise.SqlScenario;
import play.api.mvc.Call;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Result;
import play.twirl.api.Html;

public class SqlAdmin extends AbstractAdminController<SqlScenario, SqlScenarioReader> {

  // private Database sqlSelect;
  // private Database sqlOther;

  @Inject
  public SqlAdmin(FormFactory theFactory, @NamedDatabase("sqlselectroot") Database theSqlSelect,
      @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theFactory, SqlScenario.finder, "sql", new SqlScenarioReader(theSqlSelect, theSqlOther));
    // sqlSelect = theSqlSelect;
    // sqlOther = theSqlOther;
  }

  @Override
  public Result index() {
    return ok(views.html.sqlAdmin.index.render(getUser()));
  }

  @Override
  public Result newExerciseForm() {
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
  protected Call getIndex() {
    return controllers.sql.routes.SqlAdmin.index();
  }

  @Override
  protected SqlScenario initRemainingExFromForm(int id, String title, String author, String text, DynamicForm form) {
    String scriptFile = form.get(StringConsts.SCRIPTFILE_NAME);
    String shortName = form.get(StringConsts.SHORTNAME_NAME);
    return new SqlScenario(id, title, author, text, shortName, scriptFile, Collections.emptyList());
  }

}
