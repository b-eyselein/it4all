package model;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlCorrector {
  
  public static boolean correct(String statement, SqlExercise exercise, Connection conn) {
    try {
      SqlQueryResult userResult = new SqlQueryResult(conn.createStatement().executeQuery(statement));
      SqlQueryResult sampleResult = new SqlQueryResult(conn.createStatement().executeQuery(exercise.sample));

      return userResult.isIdentic(sampleResult);
    } catch (SQLException e) {
      return false;
    }
  }

}
