package model;

import java.sql.Connection;

import model.exercise.Success;
import model.queryCorrectors.SelectCorrector;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Select;

public class SqlCorrector {
  
  private static SelectCorrector selectCor = new SelectCorrector();
  
  private static DeleteCorrector deleteCor = new DeleteCorrector();
  
  private static CreateCorrector createCor = new CreateCorrector();
  
  public static SqlCorrectionResult correct(String statement, SqlExercise exercise, Connection connection) {
    // Steps taken:
    // 1. preprocess statement --> syntax
    // 2. compare statement to sample statement
    // 3. execute statement, compare results
    
    // FIXME: preprocess Statement!
    try {
      Statement parsedStatement = CCJSqlParserUtil.parse(statement);
      
      if(parsedStatement instanceof Select)
        return selectCor.correct((Select) parsedStatement, exercise, connection);
      
      else if(parsedStatement instanceof Delete)
        return deleteCor.correct((Delete) parsedStatement, exercise, connection);
      
      else if(parsedStatement instanceof CreateTable)
        return createCor.correct((CreateTable) parsedStatement, exercise, connection);
      
      else
        return new SqlCorrectionResult(Success.NONE, "Query war vom Typ \"" + parsedStatement.getClass().getSimpleName()
            + "\", der nicht korrigiert werden kann!");
      
    } catch (JSQLParserException e) {
      return new SqlCorrectionResult(Success.NONE, "Query konnte nicht geparst werden: " + e.getCause().getMessage());
    }
    
  }
  
}
