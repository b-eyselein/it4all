package controllers.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.Secured;
import model.user.User;
import play.Logger;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import play.twirl.api.Html;
import views.html.error;
import views.html.sql;

@Authenticated(Secured.class)
public class SQL extends Controller {
  
  private static final String DB_BASENAME = "sql_";

  private static final Logger.ALogger theLogger = Logger.of("application");

  @Inject
  // IMPORTANT: DO NOT USE "DEFAULT" DATABASE
  @NamedDatabase("sqltest")
  Database db;

  public Result index() {
    
    User user = UserManagement.getCurrentUser();
    try {
      Connection connection = db.getConnection();

      // Check if slave DB exists, if not, create
      String slaveDB = DB_BASENAME + user.name;
      if(!databaseAlreadyExists(connection, slaveDB))
        createDatabase(connection, slaveDB);
      else
        theLogger.debug("Database " + slaveDB + " already exists");
      
      Html result = new Html("");

      // Change db to users own db
      connection.setCatalog(slaveDB);
      theLogger.debug(connection.getCatalog());

      connection.close();
      return ok(sql.render(result, UserManagement.getCurrentUser()));

    } catch (SQLException e) {
      return badRequest(error.render(user, new Html("Fehler bei Verarbeitung: " + e.getMessage() + "!")));
    }

  }

  private void createDatabase(Connection connection, String slaveDB) {
    theLogger.debug("Trying to create the new database " + slaveDB);
    String createStatement = "CREATE DATABASE " + slaveDB;
    try {
      connection.createStatement().executeUpdate(createStatement);
    } catch (SQLException e) {
      theLogger.error("Could not create database " + slaveDB, e);
    }
  }

  private boolean databaseAlreadyExists(Connection connection, String slaveDB) {
    List<String> existingDatabases = queryExistingDatabases(connection);
    return existingDatabases.contains(slaveDB);
  }

  private List<String> queryExistingDatabases(Connection connection) {
    List<String> existingDatabases = new LinkedList<String>();
    try {
      ResultSet existingDBs = connection.createStatement().executeQuery("SHOW DATABASES");
      while(existingDBs.next())
        existingDatabases.add(existingDBs.getString(1));
    } catch (SQLException e) {
      theLogger.error("Error at creating existing databases", e);
    }
    return existingDatabases;
  }
}
