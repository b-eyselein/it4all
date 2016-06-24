package model.queryCorrectors;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.SqlCorrectionResult;
import model.TableComparisonResult;
import model.exercise.SqlExercise.SqlExType;
import model.exercise.Success;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.Logger;

public class CreateCorrector extends QueryCorrector<CreateTable> {

  public CreateCorrector() {
    super(SqlExType.CREATE);
  }

  @Override
  protected TableComparisonResult compareUsedTables(CreateTable parsedStatement, CreateTable parsedSampleStatement) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected SqlCorrectionResult executeQuery(CreateTable parsedStatement, CreateTable parsedSampleStatement,
      Connection connection, String slaveDB) {

    try {
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
