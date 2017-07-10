package model.querycorrectors.select;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.CorrectionException;
import model.StringConsts;
import model.exercise.SqlExercise;
import model.matching.Match;
import model.matching.MatchingResult;
import model.querycorrectors.ColumnMatch;
import model.querycorrectors.QueryCorrector;
import model.querycorrectors.SqlExecutionResult;
import model.querycorrectors.SqlResult;
import model.sql.SqlQueryResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import play.db.Database;

@Singleton
public class SelectCorrector extends QueryCorrector<Select, SelectItem> {

  private static final SelectColumnMatcher COL_MATCHER = new SelectColumnMatcher();

  private static final GroupByMatcher GROUP_BY_MATCHER = new GroupByMatcher();

  private static final OrderByMatcher ORDER_BY_MATCHER = new OrderByMatcher();

  public SelectCorrector() {
    super("SELECT");
  }

  protected static MatchingResult<Expression, Match<Expression>> compareGroupByElements(Select plainUserQuery,
      Select plainSampleQuery) {
    List<Expression> group1 = ((PlainSelect) plainUserQuery.getSelectBody()).getGroupByColumnReferences();
    List<Expression> group2 = ((PlainSelect) plainSampleQuery.getSelectBody()).getGroupByColumnReferences();

    if(group1 == null && group2 == null)
      return null;

    return GROUP_BY_MATCHER.match(group1 != null ? group1 : Collections.emptyList(),
        group2 != null ? group2 : Collections.emptyList());
  }

  protected static MatchingResult<OrderByElement, OrderByMatch> compareOrderByElements(Select plainUserQuery,
      Select plainSampleQuery) {
    List<OrderByElement> order1 = ((PlainSelect) plainUserQuery.getSelectBody()).getOrderByElements();
    List<OrderByElement> order2 = ((PlainSelect) plainSampleQuery.getSelectBody()).getOrderByElements();

    if(order1 == null && order2 == null)
      return null;

    return ORDER_BY_MATCHER.match(order1 != null ? order1 : Collections.emptyList(),
        order2 != null ? order2 : Collections.emptyList());
  }

  private SqlQueryResult executeStatement(Select select, Connection conn) throws CorrectionException {
    try(Statement statement = conn.createStatement()) {
      return new SqlQueryResult(statement.executeQuery(select.toString()));
    } catch (SQLException e) {
      throw new CorrectionException(select.toString(), "Es gab einen Fehler bei der Ausführung des Statements ", e);
    }
  }

  @Override
  protected MatchingResult<SelectItem, ColumnMatch<SelectItem>> compareColumns(Select userQuery,
      Map<String, String> userTableAliases, Select sampleQuery, Map<String, String> sampleTableAliases) {
    return COL_MATCHER.match(StringConsts.COLUMNS_NAME, getColumns(userQuery), getColumns(sampleQuery));
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
      throw new CorrectionException(userStatement.toString(), e.getMessage(), e);
    }

  }

  protected List<SelectItem> getColumns(Select select) {
    return ((PlainSelect) select.getSelectBody()).getSelectItems();
  }

  @Override
  protected List<String> getTableNames(Select select) {
    return getTables(select).stream().map(Table::getName).collect(Collectors.toList());
  }

  @Override
  protected List<Table> getTables(Select query) {
    List<Table> tables = new LinkedList<>();

    PlainSelect plain = (PlainSelect) query.getSelectBody();

    if(plain.getFromItem() instanceof Table)
      tables.add((Table) plain.getFromItem());

    if(plain.getJoins() != null)
      plain.getJoins().stream().filter(join -> join.getRightItem() instanceof Table)
          .forEach(join -> tables.add((Table) join.getRightItem()));

    return tables;

  }

  @Override
  protected Expression getWhere(Select select) {
    return ((PlainSelect) select.getSelectBody()).getWhere();
  }

  @Override
  protected SqlResult<Select, SelectItem> instantiateResult(String learnerSolution) {
    return new SqlResult<>(learnerSolution);
  }

  @Override
  protected List<MatchingResult<? extends Object, ? extends Match<? extends Object>>> makeOtherComparisons(Select userQ,
      Select sampleQ) {
    MatchingResult<Expression, Match<Expression>> groupByComparison = compareGroupByElements(userQ, sampleQ);
    MatchingResult<OrderByElement, OrderByMatch> orderByComparison = compareOrderByElements(userQ, sampleQ);
    return Arrays.asList(groupByComparison, orderByComparison);
  }

}
