package model.queryCorrectors;

import model.SqlCorrectionException;
import model.correctionResult.SqlCorrectionResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.update.Update;
import play.db.Database;

public class UpdateCorrector extends QueryCorrector<Update> {

  @Override
  protected SqlCorrectionResult compareStatically(Update parsedUserStatement, Update parsedSampleStatement) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected SqlCorrectionResult executeQuery(Database database, Update userStatement, Update sampleStatement,
      String scenarioName) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected Update parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (Update) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde UPDATE!");
    }
  }

}
