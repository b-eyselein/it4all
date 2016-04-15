package controllers.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import model.user.Secured;
import model.user.User;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import controllers.core.UserControl;
import views.html.sql;

@Authenticated(Secured.class)
public class SQL extends Controller {
  
  private static final String DB_BASENAME = "sql_";
  
  @Inject
  // @NamedDatabase("sqltest")
  Database db;
  
  public Result index() {
    
    User user = UserControl.getUser();
    
    String sqlStatement = "select * from task";
    
    try {
      Connection connection = db.getConnection();
      
      // Change db to users own db
      connection.setCatalog(DB_BASENAME + user.getName());
      
      // Execute query
      ResultSet resultSet = connection.createStatement().executeQuery(sqlStatement);
      ResultSetMetaData metadata = resultSet.getMetaData();
      
      // Syso result of query
      List<List<String>> result = new LinkedList<List<String>>();
      
      while(resultSet.next()) {
        List<String> row = new LinkedList<String>();
        for(int columnCount = 1; columnCount <= metadata.getColumnCount(); columnCount++)
          row.add(resultSet.getObject(columnCount).toString());
        result.add(row);
      }
      connection.close();
      return ok(sql.render(result, UserControl.getUser()));
      
    } catch (SQLException e) {
      return badRequest("Fehler bei Verarbeitung: " + e.getMessage() + "!");
    }
    
  }
}
