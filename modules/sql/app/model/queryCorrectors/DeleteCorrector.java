package model.queryCorrectors;

import java.util.List;

import model.SqlCorrectionException;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;
import play.db.Database;

public class DeleteCorrector extends QueryCorrector<Delete, Delete> {
  
  @Override
  protected List<EvaluationResult> compareStatically(Delete parsedUserStatement, Delete parsedSampleStatement,
      FeedbackLevel feedbackLevel) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected EvaluationResult executeQuery(Database database, Delete parsedUserStatement, Delete parsedSampleStatement,
      SqlExercise exercise, FeedbackLevel feedbackLevel) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected List<String> getColumns(Delete statement) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<String> getTables(Delete userQuery) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected Delete parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (Delete) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde DELETE!");
    }
  }
}
