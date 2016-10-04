package model.queryCorrectors;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.SqlCorrectionException;
import model.correctionResult.create.ColumnDefinitionResult;
import model.correctionResult.create.CreateResult;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.GenericEvaluationResult;
import model.exercise.SqlExercise;
import model.exercise.Success;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.Logger;
import play.db.Database;

public class CreateCorrector extends QueryCorrector<CreateTable, CreateTable> {

  private static ColumnDefinitionResult compareColumnDataType(String datatypeName, ColDataType userType,
      ColDataType sampleType) {
    String userDataType = userType.getDataType().toUpperCase();
    String sampleDataType = sampleType.getDataType().toUpperCase();
    if(!userDataType.equals(sampleDataType))
      return new ColumnDefinitionResult(Success.NONE, datatypeName,
          "Datentyp \"" + userDataType + "\" ist nicht korrekt, erwartet wurde \"" + sampleDataType + "\"!");

    // TODO: Compare argumentslist?
    List<String> userArgs = userType.getArgumentsStringList(), sampleArgs = sampleType.getArgumentsStringList();
    if(userArgs != null && sampleArgs != null) {
      if(userArgs.size() != sampleArgs.size())
        return new ColumnDefinitionResult(Success.PARTIALLY, datatypeName,
            "Anzahl der Argumente stimmen nicht überein!");
      for(int i = 0; i < userArgs.size(); i++)
        if(!userArgs.get(i).equals(sampleArgs.get(i)))
          return new ColumnDefinitionResult(Success.PARTIALLY, datatypeName, "Argument des Datentyps ("
              + userArgs.get(i) + ") ist nicht korrekt, erwartet wurde: (" + sampleArgs.get(i) + ")!");
    }

    return new ColumnDefinitionResult(Success.COMPLETE, datatypeName, "Datentyp richtig spezifiziert.");
  }

  private static ColumnDefinitionResult compareColumnDefinition(ColumnDefinition userDef, ColumnDefinition sampleDef) {
    ColumnDefinitionResult datatypeComparison = compareColumnDataType(userDef.getColumnName(), userDef.getColDataType(),
        sampleDef.getColDataType());

    // TODO: compare columnspecstrings!
    Logger.debug("ColumnSpecString for column " + userDef.getColumnName() + " :: " + userDef.getColumnSpecStrings());
    return datatypeComparison;
  }

  @Override
  protected List<EvaluationResult> compareStatically(CreateTable userStatement, CreateTable sampleStatement,
      FeedbackLevel feedbackLevel) {
    List<ColumnDefinition> userDefs = userStatement.getColumnDefinitions();
    List<ColumnDefinition> sampleDefs = sampleStatement.getColumnDefinitions();

    List<ColumnDefinitionResult> columnResults = new LinkedList<>();

    // FIXME: Use Matcher!

    for(final Iterator<ColumnDefinition> userIter = userDefs.iterator(); userIter.hasNext();) {
      ColumnDefinition userDef = userIter.next();
      for(Iterator<ColumnDefinition> sampleIter = sampleDefs.iterator(); sampleIter.hasNext();) {
        ColumnDefinition sampleDef = sampleIter.next();
        if(userDef.getColumnName().equals(sampleDef.getColumnName())) {
          columnResults.add(compareColumnDefinition(userDef, sampleDef));
          userIter.remove();
          sampleIter.remove();
        }
      }
    }

    // FIXME: compare primary key / foreign key definition
    Logger.debug("Indexes userStatement: " + userStatement.getIndexes());
    Logger.debug("Indexes sampleStatement: " + sampleStatement.getIndexes());

    // Remaining sampleDefs are missing, remaining userDefs are wrong
    List<String> missingColumns = sampleDefs.stream().map(def -> def.getColumnName()).collect(Collectors.toList());
    List<String> wrongColumns = userDefs.stream().map(def -> def.getColumnName()).collect(Collectors.toList());
    return Arrays.asList(new CreateResult(columnResults, missingColumns, wrongColumns));
  }

  @Override
  protected EvaluationResult executeQuery(Database database, CreateTable userStatement, CreateTable sampleStatement,
      SqlExercise exercise, FeedbackLevel feedbackLevel) {
    // TODO Auto-generated method stub
    // DO NOT EXECUTE QUERY!
    return new GenericEvaluationResult(Success.COMPLETE, "Create-Statements werden nicht ausgeführt.");
  }

  @Override
  protected List<String> getColumns(CreateTable statement) {
    return Collections.emptyList();
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
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde CREATE TABLE!");
    }
  }
}
