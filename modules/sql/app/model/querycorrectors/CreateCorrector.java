package model.querycorrectors;

import java.util.Arrays;
import java.util.List;

import model.SqlCorrectionException;
import model.correctionresult.SqlExecutionResult;
import model.exercise.SqlExercise;
import model.matching.MatchingResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.db.Database;

public class CreateCorrector extends QueryCorrector<CreateTable, CreateTable> {

  // private static ColumnDefinitionMatcher colDefMatcher = new
  // ColumnDefinitionMatcher();

  public CreateCorrector(boolean theCompareColumns, boolean theCompareOrderBy, boolean theCompareGroupBy,
      boolean theCompareWhere, boolean theExecute) {
    super("CREATE TABLE", theCompareColumns, theCompareOrderBy, theCompareGroupBy, theCompareWhere, theExecute);
  }

  @Override
  protected MatchingResult<String> compareColumns(CreateTable userQuery, CreateTable sampleQuery) {
    // No columns to compare...
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

  @Override
  protected SqlExecutionResult executeQuery(Database database, CreateTable userStatement, CreateTable sampleStatement,
      SqlExercise exercise) {
    return null;
  }

  @Override
  protected CreateTable getPlainStatement(CreateTable query) {
    return query;
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
