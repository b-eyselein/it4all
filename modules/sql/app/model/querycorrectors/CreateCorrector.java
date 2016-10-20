package model.querycorrectors;

import java.util.Arrays;
import java.util.List;

import model.SqlCorrectionException;
import model.correctionresult.ColumnComparison;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.GenericEvaluationResult;
import model.exercise.SqlExercise;
import model.exercise.Success;
import model.matcher.ColumnDefinitionMatch;
import model.matcher.ColumnDefinitionMatcher;
import model.matching.MatchingResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.db.Database;

public class CreateCorrector extends QueryCorrector<CreateTable, CreateTable> {
  
  private static ColumnDefinitionMatcher colDefMatcher = new ColumnDefinitionMatcher();
  
  @Override
  protected ColumnComparison compareColumns(CreateTable userQuery, CreateTable sampleQuery) {
    // No columns to compare...
    return null;
  }
  
  @Override
  protected List<EvaluationResult> compareStatically(CreateTable userStatement, CreateTable sampleStatement,
      FeedbackLevel feedbackLevel) {
    List<ColumnDefinition> userDefs = userStatement.getColumnDefinitions();
    List<ColumnDefinition> sampleDefs = sampleStatement.getColumnDefinitions();
    
    MatchingResult<ColumnDefinition, ColumnDefinitionMatch> result = colDefMatcher.match(userDefs, sampleDefs);
    return Arrays.asList(result);
  }
  
  @Override
  protected EvaluationResult executeQuery(Database database, CreateTable userStatement, CreateTable sampleStatement,
      SqlExercise exercise, FeedbackLevel feedbackLevel) {
    // DO NOT EXECUTE QUERY!
    return new GenericEvaluationResult(FeedbackLevel.MINIMAL_FEEDBACK, Success.COMPLETE,
        "Create-Statements werden nicht ausgeführt.");
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
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements:</p><p>" + statement,
          e);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde CREATE TABLE!", e);
    }
  }
}
