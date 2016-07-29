package model.queryCorrectors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.SqlCorrectionException;
import model.SqlQueryResult;
import model.correctionResult.SqlCorrectionResult;
import model.correctionResult.UsedColumnsComparison;
import model.correctionResult.UsedTablesComparison;
import model.exercise.Success;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import play.db.Database;

@Singleton
public class SelectCorrector extends QueryCorrector<Select> {

  private UsedColumnsComparison compareQueriedColumns(PlainSelect plainUserSelect, PlainSelect plainSampleSelect) {
    List<String> userColumns = getQueriedColumns(plainUserSelect);
    List<String> sampleColumns = getQueriedColumns(plainSampleSelect);

    List<String> wrongColumns = listDifference(userColumns, sampleColumns);
    List<String> missingColumns = listDifference(sampleColumns, userColumns);

    Success success = Success.NONE;
    if(wrongColumns.isEmpty() && missingColumns.isEmpty())
      success = Success.COMPLETE;

    return new UsedColumnsComparison(success, missingColumns, wrongColumns);
  }

  private UsedTablesComparison compareTables(PlainSelect userSelect, PlainSelect sampleSelect) {
    List<String> userTableNames = getNamesOfUsedTables(userSelect);
    List<String> sampleTableNames = getNamesOfUsedTables(sampleSelect);

    List<String> unneccessaryTables = listDifference(userTableNames, sampleTableNames);
    List<String> missingTables = listDifference(sampleTableNames, userTableNames);

    Success success = Success.NONE;
    if(missingTables.isEmpty())
      success = Success.PARTIALLY;
    if(unneccessaryTables.isEmpty())
      success = Success.COMPLETE;

    return new UsedTablesComparison(success, missingTables, unneccessaryTables);
  }

  private List<String> getNamesOfUsedTables(PlainSelect select) {
    List<String> userFromItems = new LinkedList<>();

    // Main table in Query
    if(select.getFromItem() instanceof Table)
      userFromItems.add(((Table) select.getFromItem()).getName());

    // All joined tables
    if(select.getJoins() != null)
      for(Join join: select.getJoins())
        if(join.getRightItem() instanceof Table)
          userFromItems.add(((Table) join.getRightItem()).getName());

    return userFromItems;
  }

  private List<String> getQueriedColumns(PlainSelect plainSelect) {
    return plainSelect.getSelectItems().stream().map(item -> item.toString().toUpperCase())
        .collect(Collectors.toList());
  }

  @Override
  protected SqlCorrectionResult compareStatically(Select parsedUserStatement, Select parsedSampleStatement) {
    Success success = Success.COMPLETE;
    List<String> messages = new LinkedList<>();

    PlainSelect plainUserSelect = (PlainSelect) parsedUserStatement.getSelectBody();
    PlainSelect plainSampleSelect = (PlainSelect) parsedSampleStatement.getSelectBody();

    UsedTablesComparison tableComp = compareTables(plainUserSelect, plainSampleSelect);
    // comparison has "lower" success than assumed at the moment
    if(success.compareTo(tableComp.getSuccess()) > 0)
      success = tableComp.getSuccess();

    UsedColumnsComparison columnComp = compareQueriedColumns(plainUserSelect, plainSampleSelect);
    // comparison has "lower" success than assumed at the moment
    if(success.compareTo(columnComp.getSuccess()) > 0)
      success = columnComp.getSuccess();

    // @formatter:off
    return new SqlCorrectionResult(success, messages)
        .withTableComparisonResult(tableComp)
        .withColumnsComparisonResult(columnComp);
    // @formatter:on
  }

  @Override
  protected SqlCorrectionResult executeQuery(Database database, Select userStatement, Select sampleStatement,
      String scenarioName) {
    try {
      Connection conn = database.getConnection();
      conn.setCatalog(scenarioName);

      ResultSet userResultSet = conn.createStatement().executeQuery(userStatement.toString());
      SqlQueryResult userResult = new SqlQueryResult(userResultSet, true);

      ResultSet sampleResultSet = conn.createStatement().executeQuery(sampleStatement.toString());
      SqlQueryResult sampleResult = new SqlQueryResult(sampleResultSet, true);
      
      conn.close();

      // @formatter:off
      if(userResult.isIdentic(sampleResult))
        return new SqlCorrectionResult(Success.COMPLETE, "Resultate waren mit Resultaten der Musterlösung identisch.")
            .withExecutionResult(userResult, null);
      else
        return new SqlCorrectionResult(Success.NONE, "Resultate waren nicht identisch!").
            withExecutionResult(userResult, sampleResult);
    } catch (SQLException e) {
      return new SqlCorrectionResult(Success.NONE, "Es gab ein Problem beim Ausführen der Query: " + e.getMessage());
    }
    // @formatter:on
  }

  @Override
  protected Select parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (Select) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde SELECT!");
    }
  }

}
