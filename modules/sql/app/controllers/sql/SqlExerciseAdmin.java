package controllers.sql;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AExerciseAdminController;
import model.SqlExerciseReader;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseType;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Result;
import play.twirl.api.Html;

public class SqlExerciseAdmin extends AExerciseAdminController<SqlExercise> {

  @Inject
  public SqlExerciseAdmin(FormFactory theFactory, @NamedDatabase("sqlselectroot") Database theSqlSelect,
      @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theFactory, SqlExercise.finder, SqlExerciseReader.getInstance());
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
  public Html renderCreated(List<SqlExercise> created) {
    // Guaranteed to be always one scenario by json Schema!
    return views.html.sqlAdmin.sqlCreation.render(null /* created.get(0) */);
  }

  public Result scenarioAdmin(int id) {
    return ok(views.html.sqlAdmin.scenarioAdmin.render(getUser(),
        null/* , finder.byId(id) */));
  }

  @Override
  protected SqlExercise initRemainingExFromForm(int id, String title, String author, String text, DynamicForm form) {
    SqlExerciseType exerciseType = null;
    // String scriptFile = form.get(StringConsts.SCRIPTFILE_NAME);
    // String shortName = form.get(StringConsts.SHORTNAME_NAME);
    return new SqlExercise(id, title, author, text,
        exerciseType/* , shortName, scriptFile, Collections.emptyList() */);
  }

}
