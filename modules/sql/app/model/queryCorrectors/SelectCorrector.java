package model.queryCorrectors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.SqlCorrectionException;
import model.SqlQueryResult;
import model.correctionResult.GroupByComparison;
import model.correctionResult.OrderByComparison;
import model.correctionResult.SqlExecutionResult;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.GenericEvaluationResult;
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

  private EvaluationResult compareGroupByElements(PlainSelect plainUserQuery, PlainSelect plainSampleQuery) {
    List<String> userElements = groupByElementsAsStrings(plainUserQuery);
    List<String> sampleElements = groupByElementsAsStrings(plainSampleQuery);

    if(userElements.isEmpty() && sampleElements.isEmpty())
      return new GenericEvaluationResult(FeedbackLevel.MEDIUM_FEEDBACK, Success.COMPLETE,
          "Es waren keine Group By-Elemente anzugeben.");

    List<String> wrong = listDifference(userElements, sampleElements);
    List<String> missing = listDifference(sampleElements, userElements);

    return new GroupByComparison(missing, wrong);
  }

  private EvaluationResult compareOrderByElements(PlainSelect plainUserQuery, PlainSelect plainSampleQuery) {
    if(plainUserQuery.getWhere() == null && plainSampleQuery.getWhere() == null)
      return new GenericEvaluationResult(FeedbackLevel.MEDIUM_FEEDBACK, Success.COMPLETE,
          "Es waren keine Order By-Elemente anzugeben.");

    List<String> userElements = orderByElementsAsStrings(plainUserQuery);
    List<String> sampleElements = orderByElementsAsStrings(plainSampleQuery);

    List<String> wrong = listDifference(userElements, sampleElements);
    List<String> missing = listDifference(sampleElements, userElements);

    return new OrderByComparison(missing, wrong);
  }

  private List<String> groupByElementsAsStrings(PlainSelect statement) {
    if(statement.getGroupByColumnReferences() == null)
      // TODO: behebe FIX!
      return Collections.emptyList();

    return statement.getGroupByColumnReferences().stream().map(gb -> gb.toString()).collect(Collectors.toList());
  }

  private List<String> orderByElementsAsStrings(PlainSelect statement) {
    if(statement.getOrderByElements() == null)
      // TODO: behebe FIX!
      return Collections.emptyList();

    return statement.getOrderByElements().stream().map(el -> el.toString()).collect(Collectors.toList());
  }

  @Override
  protected List<EvaluationResult> compareStatically(Select userQuery, Select sampleQuery,
      FeedbackLevel feedbackLevel) {
    PlainSelect plainUserQuery = (PlainSelect) userQuery.getSelectBody();
    PlainSelect plainSampleQuery = (PlainSelect) sampleQuery.getSelectBody();

    EvaluationResult tableComparison = compareTables(plainUserQuery, plainSampleQuery);

    EvaluationResult columnComparison = compareColumns(plainUserQuery, plainSampleQuery);

    EvaluationResult whereComparison = compareWheres(plainUserQuery, plainSampleQuery);

    EvaluationResult orderByComparison = compareOrderByElements(plainUserQuery, plainSampleQuery);

    EvaluationResult groupByComparison = compareGroupByElements(plainUserQuery, plainSampleQuery);

    return Arrays.asList(tableComparison, columnComparison, whereComparison, orderByComparison, groupByComparison);
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
  protected List<String> getColumns(PlainSelect plainSelect) {
    return plainSelect.getSelectItems().stream().map(item -> item.toString()).collect(Collectors.toList());
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
