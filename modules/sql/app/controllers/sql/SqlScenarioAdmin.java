package controllers.sql;

import javax.inject.Inject;

import controllers.core.AExerciseCollectionAdminController;
import model.SqlScenarioReader;
import model.exercise.SqlExercise;
import model.exercise.SqlScenario;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Result;

public class SqlScenarioAdmin extends AExerciseCollectionAdminController<SqlExercise, SqlScenario> {
  
  @Inject
  public SqlScenarioAdmin(FormFactory theFactory, @NamedDatabase("sqlselectroot") Database theSqlSelect,
      @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theFactory, SqlScenario.finder, new SqlScenarioReader(theSqlSelect, theSqlOther));
  }

  public Result index() {
    return ok(views.html.sqlAdmin.index.render(getUser()));
  }
}
