package controllers.sql;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AExerciseCollectionAdminController;
import model.SqlScenarioReader;
import model.exercise.SqlExercise;
import model.exercise.SqlScenario;
import model.user.User;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Result;
import play.twirl.api.Html;

public class SqlAdmin extends AExerciseCollectionAdminController<SqlExercise, SqlScenario> {
  
  @Inject
  public SqlAdmin(FormFactory theFactory, @NamedDatabase("sqlselectroot") Database theSqlSelect,
      @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theFactory, SqlToolObject$.MODULE$, SqlExercise.finder, SqlScenario.finder,
        new SqlScenarioReader(theSqlSelect, theSqlOther));
  }
  
  @Override
  public Result adminIndex() {
    return ok(views.html.sqlAdmin.index.render(getUser()));
  }
  
  // @Override
  // public Result newExerciseForm() {
  // return ok(views.html.sqlAdmin.newExerciseForm.render(getUser()));
  // }
  
  @Override
  public Html renderCollectionCreated(List<SqlScenario> created) {
    return views.html.sqlAdmin.sqlCreation.render(created);
  }
  
  public Result scenarioAdmin(int id) {
    return ok(views.html.sqlAdmin.scenarioAdmin.render(getUser(), collectionFinder.byId(id)));
  }
  
  @Override
  protected Html renderExCollCreationForm(User user, SqlScenario scenario) {
    return views.html.sqlAdmin.newScenarioForm.render(getUser(), scenario);
  }
  
  @Override
  protected Html renderExEditForm(User user, SqlExercise exercise, boolean isCreation) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected Html renderExerciseCollections(User user, List<SqlScenario> allCollections) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
