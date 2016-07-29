package model.queryCorrectors;

import model.SqlCorrectionException;
import model.correctionResult.SqlCorrectionResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.insert.Insert;
import play.db.Database;

public class InsertCorrector extends QueryCorrector<Insert> {
  
  @Override
  protected SqlCorrectionResult compareStatically(Insert parsedUserStatement, Insert parsedSampleStatement) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected SqlCorrectionResult executeQuery(Database database, Insert userStatement, Insert sampleStatement,
      String scenarioName) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected Insert parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (Insert) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde INSERT!");
    }
  }
}
