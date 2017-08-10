package model.querycorrectors.change;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import model.CorrectionException;
import model.StringConsts;
import model.exercise.SqlExercise;
import model.matching.Match;
import model.matching.MatchingResult;
import model.querycorrectors.QueryCorrector;
import model.querycorrectors.SqlExecutionResult;
import model.querycorrectors.SqlResult;
import model.sql.SqlQueryResult;
import net.sf.jsqlparser.schema.Column;
import play.Logger;
import play.db.Database;

public abstract class ChangeCorrector<Q extends net.sf.jsqlparser.statement.Statement>
    extends QueryCorrector<Q, Column> {
  
  public ChangeCorrector(String theQueryType) {
    super(theQueryType);
  }
  
  private static void runUpdate(Connection connection, String query) {
    try(Statement statement = connection.createStatement()) {
      statement.executeUpdate(query);
    } catch (SQLException e) {
      Logger.error("There has been an error running an sql query: \"" + query + "\"", e);
    }
  }
  
  private static SqlQueryResult runValidationQuery(Connection connection, String query) throws CorrectionException {
    try(Statement statement = connection.createStatement()) {
      return new SqlQueryResult(statement.executeQuery(query));
    } catch (SQLException e) {
      throw new CorrectionException(query, "There has been an error running an sql query", e);
    }
  }
  
  private SqlQueryResult getResultSet(Q statement, Connection connection, String validation)
      throws CorrectionException, SQLException {
    
    runUpdate(connection, statement.toString());
    
    SqlQueryResult result = runValidationQuery(connection, validation);
    
    connection.rollback();
    
    return result;
  }
  
  @Override
  protected SqlExecutionResult executeQuery(Database database, Q userStatement, Q sampleStatement, SqlExercise exercise)
      throws CorrectionException {
    try(final Connection connection = database.getConnection()) {
      
      connection.setCatalog(exercise.scenario.getShortName());
      connection.setAutoCommit(false);
      
      final String validation = StringConsts.SELECT_ALL_DUMMY + getTableNames(sampleStatement).get(0);
      
      final SqlQueryResult userResult = getResultSet(userStatement, connection, validation);
      
      final SqlQueryResult sampleResult = getResultSet(sampleStatement, connection, validation);
      
      return new SqlExecutionResult(userResult, sampleResult);
    } catch (SQLException e) {
      throw new CorrectionException(userStatement.toString(), "There has been an error running a statement", e);
    }
  }
  
  @Override
  protected SqlResult<Column> instantiateResult(String learnerSolution) {
    return new SqlResult<>(learnerSolution);
  }
  
  @Override
  protected List<MatchingResult<? extends Object, ? extends Match<? extends Object>>> makeOtherComparisons(Q userQ,
      Q sampleQ) {
    return Collections.emptyList();
  }
  
}
