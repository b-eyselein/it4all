package model.querycorrectors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.SqlCorrectionException;
import model.SqlQueryResult;
import model.correctionresult.ComparisonTwoListsOfStrings;
import model.correctionresult.SqlExecutionResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import play.db.Database;

@Singleton
public class SelectCorrector extends QueryCorrector<Select, PlainSelect> {
  
  private List<String> getColumns(PlainSelect plainSelect) {
    return listAsStrings(plainSelect.getSelectItems());
  }
  
  @Override
  protected ComparisonTwoListsOfStrings compareColumns(PlainSelect userQuery, PlainSelect sampleQuery) {
    // FIXME: keine Beachtung der Groß-/Kleinschreibung bei Vergleich! -->
    // Verwendung core --> model.result.Matcher?
    List<String> userColumns = getColumns(userQuery).stream().map(String::toUpperCase).collect(Collectors.toList());
    List<String> sampleColumns = getColumns(sampleQuery).stream().map(String::toUpperCase).collect(Collectors.toList());
    
    List<String> wrongColumns = listDifference(userColumns, sampleColumns);
    List<String> missingColumns = listDifference(sampleColumns, userColumns);
    
    return new ComparisonTwoListsOfStrings("Spalten", missingColumns, wrongColumns);
  }
  
  @Override
  protected ComparisonTwoListsOfStrings compareGroupByElements(PlainSelect plainUserQuery,
      PlainSelect plainSampleQuery) {
    if(plainUserQuery.getGroupByColumnReferences() == null && plainSampleQuery.getGroupByColumnReferences() == null)
      return null;
    
    List<String> userElements = listAsStrings(plainUserQuery.getGroupByColumnReferences());
    List<String> sampleElements = listAsStrings(plainSampleQuery.getGroupByColumnReferences());
    
    List<String> wrong = listDifference(userElements, sampleElements);
    List<String> missing = listDifference(sampleElements, userElements);
    
    return new ComparisonTwoListsOfStrings("Group By - Elemente", missing, wrong);
  }
  
  @Override
  protected ComparisonTwoListsOfStrings compareOrderByElements(PlainSelect plainUserQuery,
      PlainSelect plainSampleQuery) {
    if(plainUserQuery.getOrderByElements() == null && plainSampleQuery.getOrderByElements() == null)
      return null;
    
    List<String> userElements = listAsStrings(plainUserQuery.getOrderByElements());
    List<String> sampleElements = listAsStrings(plainSampleQuery.getOrderByElements());
    
    List<String> wrong = listDifference(userElements, sampleElements);
    List<String> missing = listDifference(sampleElements, userElements);
    
    return new ComparisonTwoListsOfStrings("Order By - Elemente", missing, wrong);
  }
  
  @Override
  protected SqlExecutionResult executeQuery(Database database, Select userStatement, Select sampleStatement,
      SqlExercise exercise, FeedbackLevel feedbackLevel) {
    SqlQueryResult userResult = null;
    SqlQueryResult sampleResult = null;
    
    try {
      Connection conn = database.getConnection();
      conn.setCatalog(exercise.scenario.shortName);
      
      ResultSet userResultSet = conn.createStatement().executeQuery(userStatement.toString());
      userResult = new SqlQueryResult(userResultSet);
      
      ResultSet sampleResultSet = conn.createStatement().executeQuery(sampleStatement.toString());
      sampleResult = new SqlQueryResult(sampleResultSet);
      
      conn.close();
    } catch (SQLException e) {
      // return new EvaluationFailed("Es gab einen Fehler beim Ausführen eines
      // Statements:<p><pre>"
      // + e.getMessage() + "</pre></p>");
    }
    
    return new SqlExecutionResult(feedbackLevel, userResult, sampleResult);
    
  }
  
  @Override
  protected PlainSelect getPlainStatement(Select query) {
    return (PlainSelect) query.getSelectBody();
  }
  
  @Override
  protected List<String> getTables(PlainSelect userQuery) {
    List<String> userFromItems = new LinkedList<>();
    
    // Main table in Query
    if(userQuery.getFromItem() instanceof Table)
      userFromItems.add(((Table) userQuery.getFromItem()).getName());
      
    // All joined tables
    // FIXME: JOIN ON - Bedingung --> getOnExpression() als Bedingung?
    if(userQuery.getJoins() != null)
      for(Join join: userQuery.getJoins())
        if(join.getRightItem() instanceof Table)
          userFromItems.add(((Table) join.getRightItem()).getName());
        
    return userFromItems;
  }
  
  @Override
  protected Expression getWhere(PlainSelect query) {
    return query.getWhere();
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
