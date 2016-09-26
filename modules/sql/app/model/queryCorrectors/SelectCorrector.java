package model.queryCorrectors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.SqlCorrectionException;
import model.SqlQueryResult;
import model.correctionResult.ColumnComparison;
import model.correctionResult.SqlExecutionResult;
import model.correctionResult.TableComparison;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.SelectExercise;
import model.queryCorrectors.where.WhereCorrector;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import play.db.Database;

@Singleton
public class SelectCorrector extends QueryCorrector<Select, PlainSelect, SelectExercise> {
  
  @Override
  protected List<EvaluationResult> compareStatically(Select parsedUserStatement, Select parsedSampleStatement,
      FeedbackLevel feedbackLevel) {
    
    PlainSelect plainUserSelect = (PlainSelect) parsedUserStatement.getSelectBody();
    PlainSelect plainSampleSelect = (PlainSelect) parsedSampleStatement.getSelectBody();
    
    TableComparison tableComparison = compareTables(plainUserSelect, plainSampleSelect);
    
    ColumnComparison columnComparison = compareColumns(plainUserSelect, plainSampleSelect);
    
    WhereCorrector whereCorrector = new WhereCorrector();
    EvaluationResult whereComparison = whereCorrector.correct(plainUserSelect.getWhere(), plainSampleSelect.getWhere());
    
    return Arrays.asList(tableComparison, columnComparison, whereComparison);
  }
  
  @Override
  protected EvaluationResult executeQuery(Database database, Select userStatement, Select sampleStatement,
      SelectExercise exercise, FeedbackLevel feedbackLevel) {
    SqlQueryResult userResult = null, sampleResult = null;
    
    try {
      Connection conn = database.getConnection();
      conn.setCatalog(exercise.scenario.shortName);
      
      ResultSet userResultSet = conn.createStatement().executeQuery(userStatement.toString());
      userResult = new SqlQueryResult(userResultSet);
      
      ResultSet sampleResultSet = conn.createStatement().executeQuery(sampleStatement.toString());
      sampleResult = new SqlQueryResult(sampleResultSet);
      
      conn.close();
    } catch (SQLException e) {
    }
    
    return new SqlExecutionResult(feedbackLevel, userResult, sampleResult);
    
  }
  
  @Override
  protected List<String> getColumns(PlainSelect plainSelect) {
    // FIXME: Why toUpperCase?
    return plainSelect.getSelectItems().stream().map(item -> item.toString().toUpperCase())
        .collect(Collectors.toList());
  }
  
  @Override
  protected List<String> getTables(PlainSelect userQuery) {
    List<String> userFromItems = new LinkedList<>();
    
    // Main table in Query
    if(userQuery.getFromItem() instanceof Table)
      userFromItems.add(((Table) userQuery.getFromItem()).getName());
    
    // All joined tables
    if(userQuery.getJoins() != null)
      for(Join join: userQuery.getJoins())
        if(join.getRightItem() instanceof Table)
          userFromItems.add(((Table) join.getRightItem()).getName());
        
    return userFromItems;
  }
  
  @Override
  protected Select parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (Select) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException(
          "Es gab folgenden Fehler beim Parsen ihres Statements: " + e.getCause().getMessage());
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde SELECT!");
    }
  }
  
}
