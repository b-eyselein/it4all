package model.queryCorrectors;

import java.util.List;

import model.SqlCorrectionException;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.insert.Insert;
import play.db.Database;

public class InsertCorrector extends QueryCorrector<Insert, Insert> {

  @Override
  protected List<EvaluationResult>

      compareStatically(Insert parsedUserStatement, Insert parsedSampleStatement, FeedbackLevel feedbackLevel) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected EvaluationResult executeQuery(Database database, Insert userStatement, Insert sampleStatement,
      SqlExercise exercise, FeedbackLevel feedbackLevel) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<String> getColumns(Insert statement) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<String> getTables(Insert userQuery) {
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
