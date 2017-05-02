package model.querycorrectors;

import java.util.Arrays;
import java.util.List;

import model.SqlCorrectionException;
import model.correctionresult.ComparisonTwoListsOfStrings;
import model.correctionresult.SqlExecutionResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.db.Database;

public class CreateCorrector extends QueryCorrector<CreateTable, CreateTable> {

  // private static ColumnDefinitionMatcher colDefMatcher = new
  // ColumnDefinitionMatcher();
  
  @Override
  protected ComparisonTwoListsOfStrings compareColumns(CreateTable userQuery, CreateTable sampleQuery) {
    // No columns to compare...
    return null;
  }
  
  @Override
  protected ComparisonTwoListsOfStrings compareGroupByElements(CreateTable userQuery, CreateTable sampleQuery) {
    return null;
  }
  
  @Override
  protected ComparisonTwoListsOfStrings compareOrderByElements(CreateTable userQuery, CreateTable sampleQuery) {
    return null;
  }
  
  @Override
  protected SqlExecutionResult executeQuery(Database database, CreateTable userStatement, CreateTable sampleStatement,
      SqlExercise exercise, FeedbackLevel feedbackLevel) {
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
  
  @Override
  protected CreateTable parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (CreateTable) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) { // NOSONAR
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements:" + statement, e);
    } catch (ClassCastException e) { // NOSONAR
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde CREATE TABLE!", e);
    }
  }
}
