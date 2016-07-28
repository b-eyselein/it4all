package controllers.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.SqlCorrector;
import model.SqlQueryResult;
import model.correctionResult.SqlCorrectionResult;
import model.exercise.SqlExercise;
import model.exercise.SqlScenario;
import model.exercise.SqlExercise.SqlExerciseKey;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import play.twirl.api.Html;
import views.html.error;
import views.html.sqlexercise;
import views.html.sqloverview;

@Authenticated(Secured.class)
public class SQL extends Controller {
  
  private static final String SHOW_ALL_TABLES = "SHOW TABLES";
  private static final String SELECT_ALL = "SELECT * FROM ";

  @Inject
  @NamedDatabase("sqlmain")
  // IMPORTANT: DO NOT USE "DEFAULT" DATABASE
  private Database sql_main;
  
  @Inject
  @NamedDatabase("sqlslave")
  private Database sql_slave;
  
  @Inject
  private FormFactory factory;
  
  @Inject
  @SuppressWarnings("unused")
  private SqlStartUpChecker checker;

  public Result commit(String scenarioName, int exerciseId) {
    User user = UserManagement.getCurrentUser();
    SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioName, exerciseId));
    
    DynamicForm form = factory.form().bindFromRequest();
    String editorContent = form.get("editorContent");
    
    try {
      Connection connection = sql_slave.getConnection();
      
      SqlCorrectionResult result = SqlCorrector.correct(user, editorContent, exercise, connection);
      
      connection.close();

      // JsonNode ret = Json.toJson(result);
      // System.out.println(Json.prettyPrint(ret));
      return ok(Json.toJson(result));
    } catch (SQLException e) {
      return badRequest(error.render(user, new Html("Fehler bei Verarbeitung: " + e.getMessage() + "!")));
    }
    
  }
  
  public Result exercise(String scenarioName, int exerciseId) {
    User user = UserManagement.getCurrentUser();
    SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioName, exerciseId));
    
    try {
      Connection connection = sql_main.getConnection();
      connection.setCatalog(scenarioName);
      List<SqlQueryResult> tables = new LinkedList<>();
      Statement statement = connection.createStatement();
      ResultSet existingDBs = statement.executeQuery(SHOW_ALL_TABLES);

      while(existingDBs.next()) {
        String tableName = existingDBs.getString(1);
        Statement selectStatement = connection.createStatement();
        ResultSet tableResult = selectStatement.executeQuery(SELECT_ALL + tableName);
        tables.add(new SqlQueryResult(tableResult, tableName));
        selectStatement.close();
      }

      statement.close();
      connection.close();
      return ok(sqlexercise.render(user, exercise, tables));
    } catch (SQLException e) {
      play.Logger.error("Es gab einen Fehler beim Auslesen der Tabellen!", e);
      return badRequest(error.render(user, new Html("Fehler beim Auslesen der Tabellen!")));
    }
  }
  
  public Result index() {
    return ok(sqloverview.render(UserManagement.getCurrentUser(), SqlScenario.finder.all()));
  }
  
}
