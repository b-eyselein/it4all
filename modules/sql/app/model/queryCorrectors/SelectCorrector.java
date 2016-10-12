package model.queryCorrectors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.SqlCorrectionException;
import model.SqlQueryResult;
import model.correctionResult.ColumnComparison;
import model.correctionResult.GroupByComparison;
import model.correctionResult.OrderByComparison;
import model.correctionResult.SqlExecutionResult;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.Success;
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

  private static <T> List<String> listAsStrings(List<T> list) {
    if(list == null)
      return Collections.emptyList();
    return list.stream().map(t -> t.toString()).collect(Collectors.toList());
  }

  private EvaluationResult compareGroupByElements(PlainSelect plainUserQuery, PlainSelect plainSampleQuery) {
    if(plainUserQuery.getGroupByColumnReferences() == null && plainSampleQuery.getGroupByColumnReferences() == null)
      return null;

    List<String> userElements = listAsStrings(plainUserQuery.getGroupByColumnReferences());
    List<String> sampleElements = listAsStrings(plainSampleQuery.getGroupByColumnReferences());

    List<String> wrong = listDifference(userElements, sampleElements);
    List<String> missing = listDifference(sampleElements, userElements);

    return new GroupByComparison(missing, wrong);
  }

  private EvaluationResult compareOrderByElements(PlainSelect plainUserQuery, PlainSelect plainSampleQuery) {
    if(plainUserQuery.getOrderByElements() == null && plainSampleQuery.getOrderByElements() == null)
      return null;

    List<String> userElements = listAsStrings(plainUserQuery.getOrderByElements());
    List<String> sampleElements = listAsStrings(plainSampleQuery.getOrderByElements());

    List<String> wrong = listDifference(userElements, sampleElements);
    List<String> missing = listDifference(sampleElements, userElements);

    return new OrderByComparison(missing, wrong);
  }

  private List<String> getColumns(PlainSelect plainSelect) {
    return plainSelect.getSelectItems().stream().map(item -> item.toString()).collect(Collectors.toList());
  }

  @Override
  protected ColumnComparison compareColumns(PlainSelect userQuery, PlainSelect sampleQuery) {
    // FIXME: keine Beachtung der Groß-/Kleinschreibung bei Vergleich! -->
    // Verwendung core --> model.result.Matcher?
    List<String> userColumns = getColumns(userQuery).stream().map(q -> q.toUpperCase()).collect(Collectors.toList());
    List<String> sampleColumns = getColumns(sampleQuery).stream().map(q -> q.toUpperCase())
        .collect(Collectors.toList());

    List<String> wrongColumns = listDifference(userColumns, sampleColumns);
    List<String> missingColumns = listDifference(sampleColumns, userColumns);

    Success success = Success.NONE;
    if(wrongColumns.isEmpty() && missingColumns.isEmpty())
      success = Success.COMPLETE;

    return new ColumnComparison(success, missingColumns, wrongColumns);
  }

  @Override
  protected List<EvaluationResult> compareStatically(Select userQuery, Select sampleQuery,
      FeedbackLevel feedbackLevel) {
    PlainSelect plainUserQuery = (PlainSelect) userQuery.getSelectBody();
    PlainSelect plainSampleQuery = (PlainSelect) sampleQuery.getSelectBody();

    List<EvaluationResult> results = new LinkedList<>();

    EvaluationResult tableComparison = compareTables(plainUserQuery, plainSampleQuery);
    results.add(tableComparison);

    EvaluationResult columnComparison = compareColumns(plainUserQuery, plainSampleQuery);
    results.add(columnComparison);

    EvaluationResult whereComparison = compareWheres(plainUserQuery, plainSampleQuery);
    if(whereComparison != null)
      results.add(whereComparison);

    EvaluationResult orderByComparison = compareOrderByElements(plainUserQuery, plainSampleQuery);
    if(orderByComparison != null)
      results.add(orderByComparison);

    EvaluationResult groupByComparison = compareGroupByElements(plainUserQuery, plainSampleQuery);
    if(groupByComparison != null)
      results.add(groupByComparison);

    return results;
  }

  @Override
  protected EvaluationResult executeQuery(Database database, Select userStatement, Select sampleStatement,
      SqlExercise exercise, FeedbackLevel feedbackLevel) {
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
      return new EvaluationFailed(
          "Es gab einen Fehler beim Ausführen eines Statements:<p><pre>" + e.getMessage() + "</pre></p>");
    }

    return new SqlExecutionResult(feedbackLevel, userResult, sampleResult);

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
