package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.SqlExercise.SqlExType;
import model.exercise.Success;
import model.queryCorrectors.QueryCorrector;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.Logger;

public class CreateCorrector extends QueryCorrector<CreateTable> {

  public CreateCorrector() {
    super(SqlExType.CREATE);
  }

  @Override
  protected SqlCorrectionResult compareUsedTables(CreateTable parsedStatement, CreateTable parsedSampleStatement) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected SqlCorrectionResult correctSpecialForQuery(CreateTable parsedStatement, SqlExercise exercise,
      Connection connection) {

    try {
      if(exercise.exType != SqlExType.CREATE)
        return new SqlCorrectionResult(Success.NONE, "Es wurde das falsche Keyword verwendet!");
      // connection.createStatement().executeQuery(statement);

      DatabaseMetaData dbmd = connection.getMetaData();
      ResultSet tables = dbmd.getTables(connection.getCatalog(), null, null, null);
      while(tables.next())
        Logger.debug(tables.getString(3));
      return new SqlCorrectionResult(Success.COMPLETE, "Passt?");
    } catch (SQLException e) {
      return new SqlCorrectionResult(Success.NONE, "Es gab ein Problem beim Ausführen der Query: " + e.getMessage());
    }

    // TODO Auto-generated method stub
  }

}
