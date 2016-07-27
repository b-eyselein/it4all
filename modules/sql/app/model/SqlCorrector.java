package model;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;

import model.correctionResult.SqlCorrectionResult;
import model.exercise.SqlExercise;
import model.exercise.Success;
import model.queryCorrectors.CreateCorrector;
import model.queryCorrectors.DeleteCorrector;
import model.queryCorrectors.QueryCorrector;
import model.queryCorrectors.SelectCorrector;
import model.user.User;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Select;

public class SqlCorrector {
  
  private static HashMap<String, QueryCorrector<? extends Statement>> correctors = new HashMap<>();
  
  static {
    correctors.put(Select.class.getSimpleName(), new SelectCorrector());
    correctors.put(Delete.class.getSimpleName(), new DeleteCorrector());
    correctors.put(CreateTable.class.getSimpleName(), new CreateCorrector());
  }
  
  /**
   * This method parses the learners solution (an sql statement) and corrects it
   * against a given exercise with one or more sample solutions. Part of the
   * correction is an execution on a real sql database.
   *
   * @param statement
   *          the learners solution, a sql statement
   * @param exercise
   *          the exercise with the sample solution
   * @param connection
   *          the connection to execute both of the queries on
   * @return SqlCorrectionResult
   */
  public static SqlCorrectionResult correct(User user, String statement, SqlExercise exercise, Connection connection) {
    
    Statement parsedStatement = null;
    try {
      parsedStatement = CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      return new SqlCorrectionResult(Success.NONE,
          Arrays.asList("Query konnte nicht geparst werden: " + e.getCause().getMessage()));
    }
    
    // Get matching Corrector to parsed Statement
    // e. g. "Select" --> SelectCorrector) from correctors
    QueryCorrector<? extends Statement> corrector = correctors.get(parsedStatement.getClass().getSimpleName());
    if(corrector == null)
      return new SqlCorrectionResult(Success.NONE, Arrays.asList("Query war vom Typ \""
          + parsedStatement.getClass().getSimpleName() + "\", der nicht korrigiert werden kann!"));
    
    return corrector.correct(user, statement, exercise, connection);
    
  }
  
}
