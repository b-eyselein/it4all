package model.queryCorrectors;

import java.util.List;

import model.SqlCorrectionException;
import model.correctionResult.SqlCorrectionResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.db.Database;

public class CreateCorrector extends QueryCorrector<CreateTable, CreateTable> {
  
  @Override
  protected SqlCorrectionResult compareStatically(CreateTable parsedUserStatement, CreateTable parsedSampleStatement) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected SqlCorrectionResult executeQuery(Database database, CreateTable parsedStatement,
      CreateTable parsedSampleStatement, String scenarioName) {
    
    return null;
  }
  
  @Override
  protected List<String> getColumns(CreateTable statement) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected List<String> getTables(CreateTable userQuery) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected CreateTable parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (CreateTable) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde CREATE TABLE!");
    }
  }
}
