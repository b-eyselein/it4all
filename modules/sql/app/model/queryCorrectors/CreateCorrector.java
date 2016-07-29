package model.queryCorrectors;

import model.SqlCorrectionException;
import model.correctionResult.SqlCorrectionResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.db.Database;

public class CreateCorrector extends QueryCorrector<CreateTable> {

  @Override
  protected SqlCorrectionResult compareStatically(CreateTable parsedUserStatement, CreateTable parsedSampleStatement) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected SqlCorrectionResult executeQuery(Database database, CreateTable parsedStatement,
      CreateTable parsedSampleStatement, String scenarioName) {
    
    // TODO Auto-generated method stub
    // try {
    // connection.createStatement().executeQuery(statement);

    // DatabaseMetaData dbmd = connection.getMetaData();
    // ResultSet tables = dbmd.getTables(connection.getCatalog(), null, null,
    // null);
    // while(tables.next())
    // Logger.debug(tables.getString(3));
    // return new SqlCorrectionResult(Success.COMPLETE, "Passt?");
    // } catch (SQLException e) {
    // return new SqlCorrectionResult(Success.NONE, "Es gab ein Problem beim
    // Ausführen der Query: " + e.getMessage());
    // }
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
