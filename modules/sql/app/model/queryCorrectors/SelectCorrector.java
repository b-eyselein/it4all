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
import model.correctionResult.ColumnComparison;
import model.correctionResult.TableComparison;
import model.exercise.Success;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import play.db.Database;

@Singleton
public class SelectCorrector extends QueryCorrector<Select, PlainSelect> {
  
  @Override
  protected SqlCorrectionResult compareStatically(Select parsedUserStatement, Select parsedSampleStatement) {
    Success success = Success.COMPLETE;
    
    PlainSelect plainUserSelect = (PlainSelect) parsedUserStatement.getSelectBody();
    PlainSelect plainSampleSelect = (PlainSelect) parsedSampleStatement.getSelectBody();
    
    TableComparison tableComparison = compareTables(plainUserSelect, plainSampleSelect);
    
    ColumnComparison columnComparison = compareColumns(plainUserSelect, plainSampleSelect);

    // comparison has "lower" success than assumed at the moment
    if(success.compareTo(tableComparison.getSuccess()) > 0)
      success = tableComparison.getSuccess();
    if(success.compareTo(columnComparison.getSuccess()) > 0)
      success = columnComparison.getSuccess();
    
    return new SqlCorrectionResult(success, columnComparison, tableComparison);
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
  protected List<String> getColumns(PlainSelect plainSelect) {
    return plainSelect.getSelectItems().stream().map(item -> item.toString().toUpperCase())
        .collect(Collectors.toList());
  }
  
  @Override
  protected List<String> getTables(PlainSelect userQuery) {
    List<String> userFromItems = new LinkedList<>();
    
    // Main table in Query
    if(userQuery.getFromItem() instanceof Table)
      userFromItems.add(((Table) userQuery.getFromItem()).getName());
    
    // All joined tables
    if(userQuery.getJoins() != null)
      for(Join join: userQuery.getJoins())
        if(join.getRightItem() instanceof Table)
          userFromItems.add(((Table) join.getRightItem()).getName());
        
    return userFromItems;
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
