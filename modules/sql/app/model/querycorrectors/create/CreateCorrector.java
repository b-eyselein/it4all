package model.querycorrectors.create;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import model.exercise.SqlExercise;
import model.matching.Match;
import model.matching.MatchingResult;
import model.querycorrectors.ColumnMatch;
import model.querycorrectors.QueryCorrector;
import model.querycorrectors.SqlExecutionResult;
import model.querycorrectors.SqlResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.db.Database;

public class CreateCorrector extends QueryCorrector<CreateTable, ColumnDefinition> {

  public CreateCorrector() {
    super("CREATE TABLE");
  }

  @Override
  protected MatchingResult<ColumnDefinition, ColumnMatch<ColumnDefinition>> compareColumns(CreateTable userQuery,
      Map<String, String> userTableAliases, CreateTable sampleQuery, Map<String, String> sampleTableAliases) {
    // TODO Auto-generated method stub
    return new CreateColumnMatcher().match("Spalten", userQuery.getColumnDefinitions(),
        sampleQuery.getColumnDefinitions());
  }

  @Override
  protected SqlExecutionResult executeQuery(Database database, CreateTable userStatement, CreateTable sampleStatement,
      SqlExercise exercise) {
    return null;
  }

  @Override
  protected List<String> getTableNames(CreateTable query) {
    return Arrays.asList(query.getTable().toString());
  }

  @Override
  protected List<Table> getTables(CreateTable query) {
    return Arrays.asList(query.getTable());
  }

  @Override
  protected Expression getWhere(CreateTable query) {
    return null;
  }

  @Override
  protected SqlResult<CreateTable, ColumnDefinition> instantiateResult(String learnerSolution) {
    return new SqlResult<>(learnerSolution);
  }
  
  @Override
  protected List<MatchingResult<? extends Object, ? extends Match<? extends Object>>> makeOtherComparisons(
      CreateTable userQ, CreateTable sampleQ) {
    return Collections.emptyList();
  }

}
