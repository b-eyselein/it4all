package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import model.SqlExercise.SqlExType;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import play.Logger;

public class SqlCorrector {
  
  private static final TablesNamesFinder TABLE_NAME_FINDER = new TablesNamesFinder();
  
  public static String correct(String statement, SqlExercise exercise, Connection connection) {
    // Steps taken:
    // 1. preprocess statement --> syntax
    // 2. compare statement to sample statement
    // 3. execute statement, compare results
    
    // FIXME: preprocess Statement!
    try {
      Statement parsedStatement = CCJSqlParserUtil.parse(statement);
      if(parsedStatement instanceof Select)
        return correctQuery((Select) parsedStatement, exercise, connection);
      else if(parsedStatement instanceof Delete)
        return correctQuery((Delete) parsedStatement, exercise, connection);
      else if(parsedStatement instanceof CreateTable)
        return correctQuery((CreateTable) parsedStatement, exercise, connection);
      else
        return parsedStatement.getClass().toString();
    } catch (JSQLParserException e) {
      Logger.error("Query could not be parsed", e.getCause());
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
  
  private static String correctQuery(Select parsedStatement, SqlExercise exercise, Connection connection) {
    try {
      if(exercise.exType != SqlExType.SELECT)
        return "Es wurde das falsche Keyword verwendet!";
      
      Select parsedSampleStatement = (Select) CCJSqlParserUtil.parse(exercise.sample);
      
      List<String> tablesInUserStatement = TABLE_NAME_FINDER.getTableList(parsedStatement);
      List<String> tablesInSampleStatement = TABLE_NAME_FINDER.getTableList(parsedSampleStatement);
      if(tablesInSampleStatement.size() != tablesInUserStatement.size())
        Logger.debug("Anzahl Tabellen ungleich!");
      
      ResultSet userResultSet = connection.createStatement().executeQuery(parsedStatement.toString());
      SqlQueryResult userResult = new SqlQueryResult(userResultSet, true);
      
      ResultSet sampleResultSet = connection.createStatement().executeQuery(exercise.sample);
      SqlQueryResult sampleResult = new SqlQueryResult(sampleResultSet, true);
      if(userResult.isIdentic(sampleResult))
        return "Passt...";
      else
        return "Fehler...";
    } catch (SQLException e) {
      return "Es gab ein Problem beim Ausführen der Query: " + e.getMessage();
    } catch (JSQLParserException e) {
      return "Es gab ein Problem beim Evaluieren gegen die Musterlösung: " + e.getMessage();
    }
  }
  
}
