package model.querycorrectors.create;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import model.correctionresult.SqlExecutionResult;
import model.exercise.SqlExercise;
import model.matching.Match;
import model.matching.MatchingResult;
import model.querycorrectors.QueryCorrector;
import model.querycorrectors.columnmatch.ColumnMatch;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.db.Database;

public class CreateCorrector extends QueryCorrector<CreateTable, ColumnDefinition> {
  
  public CreateCorrector() {
    super("CREATE TABLE");
  }
  
  @Override
  protected MatchingResult<ColumnDefinition, ColumnMatch<ColumnDefinition>> compareColumns(CreateTable userQuery,
      CreateTable sampleQuery) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected SqlExecutionResult executeQuery(Database database, CreateTable userStatement, CreateTable sampleStatement,
      SqlExercise exercise) {
    return null;
  }
  
  @Override
  protected List<String> getTables(CreateTable userQuery) {
    return Arrays.asList(userQuery.getTable().toString());
  }
  
  @Override
  protected Expression getWhere(CreateTable query) {
    return null;
  }
  
  @Override
  protected List<MatchingResult<String, Match<String>>> makeStringComparisons(CreateTable userQ, CreateTable sampleQ) {
    return Collections.emptyList();
  }
  
}
