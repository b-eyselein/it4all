package model.querycorrectors.create;

import java.util.Arrays;
import java.util.List;

import model.correctionresult.SqlExecutionResult;
import model.exercise.SqlExercise;
import model.matching.MatchingResult;
import model.querycorrectors.QueryCorrector;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.db.Database;

public class CreateCorrector extends QueryCorrector<CreateTable, ColumnDefinition> {

  // private static ColumnDefinitionMatcher colDefMatcher = new
  // ColumnDefinitionMatcher();

  public CreateCorrector() {
    super("CREATE TABLE");
  }

  @Override
  protected MatchingResult<ColumnDefinition> compareColumns(CreateTable userQuery, CreateTable sampleQuery) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected MatchingResult<String> compareGroupByElements(CreateTable userQuery, CreateTable sampleQuery) {
    return null;
  }

  @Override
  protected MatchingResult<String> compareOrderByElements(CreateTable userQuery, CreateTable sampleQuery) {
    return null;
  }

  // @Override
  // protected List<String> getColumns(CreateTable query) {
  // // TODO Auto-generated method stub
  // return null;
  // }

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

}
