package controllers.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.core.UserManagement;
import model.Secured;
import model.SqlCorrector;
import model.SqlQueryResult;
import model.correctionResult.SqlCorrectionResult;
import model.exercise.SqlExercise;
import model.exercise.SqlScenario;
import model.exercise.SqlExercise.SqlExType;
import model.exercise.SqlExercise.SqlExerciseKey;
import model.user.User;
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
  @NamedDatabase("sqlselectuser")
  // IMPORTANT: DO NOT USE "DEFAULT" DATABASE
  private Database sql_select;
  
  @Inject
  @NamedDatabase("sqlotheruser")
  private Database sql_other;
  
  @Inject
  private FormFactory factory;
  
  @Inject
  @SuppressWarnings("unused")
  private SqlStartUpChecker checker;
  
  public Result commit(String scenarioName, int exerciseId) {
    User user = UserManagement.getCurrentUser();
    SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioName, exerciseId));
    String learnerSolution = factory.form().bindFromRequest().get("editorContent");
    
    Database database = getDatabaseForExerciseType(exercise.exType);
    
    SqlCorrectionResult result = SqlCorrector.correct(database, user, learnerSolution, exercise);
    
    JsonNode ret = Json.toJson(result);
    // Logger.debug("Result as Json:\n" + Json.prettyPrint(ret));
    return ok(Json.toJson(ret));
  }
  
  public Result exercise(String scenarioName, int exerciseId) {
    User user = UserManagement.getCurrentUser();
    SqlExercise exercise = SqlExercise.finder.byId(new SqlExerciseKey(scenarioName, exerciseId));
    
    try {
      Connection connection = sql_select.getConnection();
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
  
  private Database getDatabaseForExerciseType(SqlExType exType) {
    if(exType == SqlExType.SELECT)
      return sql_select;
    return sql_other;
  }
  
}
