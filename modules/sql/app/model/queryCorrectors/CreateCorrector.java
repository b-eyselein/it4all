package model.queryCorrectors;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import model.SqlCorrectionException;
import model.correctionResult.create.ColumnDefinitionResult;
import model.correctionResult.create.CreateResult;
import model.exercise.CreateExercise;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.Logger;
import play.db.Database;

public class CreateCorrector extends QueryCorrector<CreateTable, CreateTable, CreateExercise> {
  
  private static ColumnDefinitionResult compareColumnDataType(String datatypeName, ColDataType userType,
      ColDataType sampleType) {
    String userDataType = userType.getDataType().toUpperCase();
    String sampleDataType = sampleType.getDataType().toUpperCase();
    if(!userDataType.equals(sampleDataType))
      return new ColumnDefinitionResult(Success.NONE, datatypeName,
          "Datentyp \"" + userDataType + "\" ist nicht korrekt, erwartet wurde \"" + sampleDataType + "\"!");
    
    // FIXME: Compare argumentslist?
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
  
  private static ColumnDefinitionResult compareColumnDefinition(String datatypeName, ColumnDefinition userDef,
      ColumnDefinition sampleDef) {
    Logger.debug("Comparing defined columns " + userDef.getColumnName() + " :: " + sampleDef.getColumnName());
    
    ColumnDefinitionResult datatypeComparison = compareColumnDataType(datatypeName, userDef.getColDataType(),
        sampleDef.getColDataType());
    Logger.debug("\tDatatype comparison: " + datatypeComparison.getAsHtml());
    return datatypeComparison;
  }
  
  private static Optional<ColumnDefinition> getColumnDef(List<ColumnDefinition> columnDefinitions, String columnName) {
    return columnDefinitions.stream().filter(def -> def.getColumnName().equals(columnName)).findFirst();
  }
  
  @Override
  protected List<EvaluationResult> compareStatically(CreateTable userStatement, CreateTable sampleStatement,
      FeedbackLevel feedbackLevel) {
    
    List<ColumnDefinition> userDefs = userStatement.getColumnDefinitions();
    List<ColumnDefinition> sampleDefs = sampleStatement.getColumnDefinitions();
    
    List<String> userDefColumns = getColumns(userStatement);
    List<String> sampleDefColumns = getColumns(sampleStatement);
    
    List<String> missingColumns = listDifference(sampleDefColumns, userDefColumns);
    List<String> unneccessaryColumns = listDifference(userDefColumns, sampleDefColumns);
    List<String> definedColumns = listDifference(sampleDefColumns, missingColumns);
    
    List<ColumnDefinitionResult> columnResults = new ArrayList<>(definedColumns.size());
    
    definedColumns.forEach(defCol -> {
      Optional<ColumnDefinition> userDef = getColumnDef(userDefs, defCol);
      Optional<ColumnDefinition> sampleDef = getColumnDef(sampleDefs, defCol);
      
      if(userDef.isPresent() && sampleDef.isPresent())
        columnResults.add(compareColumnDefinition(defCol, userDef.get(), sampleDef.get()));
    });
    
    // TODO Auto-generated method stub
    return Arrays.asList(new CreateResult(columnResults, missingColumns, unneccessaryColumns));
  }
  
  @Override
  protected EvaluationResult executeQuery(Database database, CreateTable parsedStatement,
      CreateTable parsedSampleStatement, CreateExercise exercise, FeedbackLevel feedbackLevel) {
    try(Connection connection = database.getConnection()) {
      connection.setCatalog(exercise.scenario.shortName);
      
      // ResultSet resultSet = connection.createStatement().executeQuery("SHOW
      // FIELDS FROM " + "Employee"); // exercise.tablename);
      // ResultSetMetaData rsmd = resultSet.getMetaData();
      // while(resultSet.next()) {
      // for(int i = 1; i <= rsmd.getColumnCount(); i++) {
      // System.out.print(resultSet.getString(i) + "\t\t");
      // }
      // System.out.println();
      // }
      
    } catch (SQLException e) {
      Logger.error("Failure:", e);
    }
    return new EvaluationFailed("NOT YET IMPLEMENTED!");
  }
  
  @Override
  protected List<String> getColumns(CreateTable statement) {
    return statement.getColumnDefinitions().stream().map(userDef -> userDef.getColumnName())
        .collect(Collectors.toList());
  }
  
  @Override
  protected List<String> getTables(CreateTable userQuery) {
    return Arrays.asList(userQuery.getTable().toString());
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
