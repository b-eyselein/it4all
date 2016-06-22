package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.SqlExercise.SqlExType;
import model.queryCorrectors.SelectCorrector;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import play.Logger;

public class SqlCorrector {
  
  public static String correct(String statement, SqlExercise exercise, Connection connection) {
    // Steps taken:
    // 1. preprocess statement --> syntax
    // 2. compare statement to sample statement
    // 3. execute statement, compare results

    // FIXME: preprocess Statement!
    try {
      Statement parsedStatement = CCJSqlParserUtil.parse(statement);
      if(parsedStatement instanceof Select)
        return (new SelectCorrector()).correct((Select) parsedStatement, exercise, connection);
      else if(parsedStatement instanceof Delete)
        return correctQuery((Delete) parsedStatement, exercise, connection);
      else if(parsedStatement instanceof CreateTable)
        return correctQuery((CreateTable) parsedStatement, exercise, connection);
      else
        return parsedStatement.getClass().toString();
    } catch (JSQLParserException e) {
      return "Query konnte nicht geparst werden: " + e.getCause().getMessage();
    }

  }

  private static String correctQuery(CreateTable statement, SqlExercise exercise, Connection connection) {
    try {
      if(exercise.exType != SqlExType.CREATE)
        return "Es wurde das falsche Keyword verwendet!";
      // connection.createStatement().executeQuery(statement);

      DatabaseMetaData dbmd = connection.getMetaData();
      ResultSet tables = dbmd.getTables(connection.getCatalog(), null, null, null);
      while(tables.next())
        Logger.debug(tables.getString(3));
      return "TOTO!";
    } catch (SQLException e) {
      return "Es gab ein Problem beim Ausführen der Query: " + e.getMessage();
    }
  }

  private static String correctQuery(Delete statement, SqlExercise exercise, Connection connection) {
    if(exercise.exType != SqlExType.DELETE)
      return "Es wurde das falsche Keyword verwendet!";
    return "TODO!";
  }

}
