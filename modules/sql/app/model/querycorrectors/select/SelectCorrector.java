package model.querycorrectors.select;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import model.SqlCorrectionException;
import model.StringConsts;
import model.correction.CorrectionException;
import model.correctionresult.SqlExecutionResult;
import model.exercise.SqlExercise;
import model.matching.MatchingResult;
import model.querycorrectors.QueryCorrector;
import model.sql.SqlQueryResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import play.db.Database;

@Singleton
public class SelectCorrector extends QueryCorrector<Select, SelectItem> {
  
  private static final SelectColumnsMatcher COL_MATCHER = new SelectColumnsMatcher();
  
  public SelectCorrector() {
    super("SELECT");
  }
  
  private SqlQueryResult executeStatement(Select select, Connection conn) throws SQLException {
    try(Statement statement = conn.createStatement()) {
      return new SqlQueryResult(statement.executeQuery(select.toString()));
    } catch (SQLException e) {
      throw e;
    }
  }
  
  @Override
  protected MatchingResult<SelectItem> compareColumns(Select userQuery, Select sampleQuery) {
    return COL_MATCHER.match(StringConsts.COLUMNS_NAME, getColumns(userQuery), getColumns(sampleQuery));
  }
  
  @Override
  protected MatchingResult<String> compareGroupByElements(Select plainUserQuery, Select plainSampleQuery) {
    return STRING_EQ_MATCHER.match("Group By-Elemente",
        listAsStrings(((PlainSelect) plainUserQuery.getSelectBody()).getGroupByColumnReferences()),
        listAsStrings(((PlainSelect) plainSampleQuery.getSelectBody()).getGroupByColumnReferences()));
  }
  
  @Override
  protected MatchingResult<String> compareOrderByElements(Select plainUserQuery, Select plainSampleQuery) {
    return STRING_EQ_MATCHER.match("Order By-Elemente",
        listAsStrings(((PlainSelect) plainUserQuery.getSelectBody()).getOrderByElements()),
        listAsStrings(((PlainSelect) plainSampleQuery.getSelectBody()).getOrderByElements()));
  }
  
  @Override
  protected SqlExecutionResult executeQuery(Database database, Select userStatement, Select sampleStatement,
      SqlExercise exercise) throws CorrectionException {
    SqlQueryResult userResult = null;
    SqlQueryResult sampleResult = null;
    
    try(Connection conn = database.getConnection()) {
      conn.setCatalog(exercise.scenario.shortName);
      
      userResult = executeStatement(userStatement, conn);
      
      sampleResult = executeStatement(sampleStatement, conn);
      
      return new SqlExecutionResult(userResult, sampleResult);
    } catch (SQLException e) {
      e.fillInStackTrace();
      throw new SqlCorrectionException(userStatement.toString(), e.getMessage(), e);
    }
    
  }
  
  protected List<SelectItem> getColumns(Select select) {
    return ((PlainSelect) select.getSelectBody()).getSelectItems();
  }
  
  @Override
  protected List<String> getTables(Select select) {
    List<String> userFromItems = new LinkedList<>();
    
    // Main table in Query
    if(((PlainSelect) select.getSelectBody()).getFromItem() instanceof Table)
      userFromItems.add(((Table) ((PlainSelect) select.getSelectBody()).getFromItem()).getName());
      
    // All joined tables
    // FIXME: JOIN ON - Bedingung --> getOnExpression() als Bedingung?
    if(((PlainSelect) select.getSelectBody()).getJoins() != null)
      for(Join join: ((PlainSelect) select.getSelectBody()).getJoins())
        if(join.getRightItem() instanceof Table)
          userFromItems.add(((Table) join.getRightItem()).getName());
        
    return userFromItems;
  }
  
  @Override
  protected Expression getWhere(Select select) {
    return ((PlainSelect) select.getSelectBody()).getWhere();
  }
  
}
