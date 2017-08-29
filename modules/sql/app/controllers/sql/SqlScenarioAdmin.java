package controllers.sql;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AExerciseCollectionAdminController;
import model.SqlScenarioReader;
import model.exercise.SqlExercise;
import model.exercise.SqlScenario;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Result;
import play.twirl.api.Html;

public class SqlScenarioAdmin extends AExerciseCollectionAdminController<SqlExercise, SqlScenario> {

  @Inject
  public SqlScenarioAdmin(FormFactory theFactory, @NamedDatabase("sqlselectroot") Database theSqlSelect,
      @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theFactory, SqlScenario.finder, new SqlScenarioReader(theSqlSelect, theSqlOther));
  }

  @Override
  public Result index() {
    return ok(views.html.sqlAdmin.index.render(getUser()));
  }

  @Override
  public Result newExerciseCollectionForm() {
    return ok(views.html.sqlAdmin.newScenarioForm.render(getUser()));
  }

  @Override
  public Result newExerciseForm() {
    return ok(views.html.sqlAdmin.newExerciseForm.render(getUser()));
  }

  @Override
  public Html renderCreated(List<SqlScenario> created) {
    // Guaranteed to be always one scenario by json Schema!
    return views.html.sqlAdmin.sqlCreation.render(created);
  }

  public Result scenarioAdmin(int id) {
    return ok(views.html.sqlAdmin.scenarioAdmin.render(getUser(), collectionFinder.byId(id)));
  }

}
