package controllers.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.inject.Inject;

import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Controller;
import play.mvc.Result;

public class SQL extends Controller {

  @Inject
  @NamedDatabase("sqltest")
  Database db;

  public Result index() {

    String sql = "select * from task";

    try {

      Connection connection = db.getConnection();
      ResultSet resultSet = connection.createStatement().executeQuery(sql);
      ResultSetMetaData metadata = resultSet.getMetaData();

      System.out.println("Anzahl Spalten: " + metadata.getColumnCount());
      
      for(int j = 1; j <= metadata.getColumnCount(); j++)
        System.out.println(metadata.getColumnName(j));

      connection.close();

    } catch (SQLException e) {
      System.out.println("FEHLER: " + e.getMessage());
    }

    return ok("TODO: SQL");
  }
}
