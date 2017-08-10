package model.querycorrectors.create;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.exercise.SqlExercise;
import model.matching.Match;
import model.matching.MatchingResult;
import model.querycorrectors.ColumnWrapper;
import model.querycorrectors.QueryCorrector;
import model.querycorrectors.SqlExecutionResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.db.Database;

public class CreateCorrector extends QueryCorrector<CreateTable> {
  
  public CreateCorrector() {
    super("CREATE TABLE");
  }

  @Override
  protected SqlExecutionResult executeQuery(Database database, CreateTable userStatement, CreateTable sampleStatement,
      SqlExercise exercise) {
    return null;
  }
  
  @Override
  protected List<ColumnWrapper> getColumnWrappers(CreateTable query) {
    return query.getColumnDefinitions().parallelStream().map(ColumnWrapper::wrap).collect(Collectors.toList());
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
  protected List<MatchingResult<? extends Object, ? extends Match<? extends Object>>> makeOtherComparisons(
      CreateTable userQ, CreateTable sampleQ) {
    return Collections.emptyList();
  }
  
}
