package model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import play.Logger;

/**
 * Tool to run database scripts
 */
public class ScriptRunner {

  private static final Logger.ALogger theLogger = Logger.of("sql");

  private static final String DELIMITER = ";";
  
  /**
   * Runs an SQL script (read in using the Reader parameter) using the
   * connection passed in
   *
   * @param connection
   *          - the connection to use for the script
   * @param reader
   *          - the source of the script
   * @throws SQLException
   *           if any SQL errors occur
   * @throws IOException
   *           if there is an error reading from the Reader
   */
  public static void runScript(Connection connection, List<String> lines, boolean autoCommit, boolean stopOnError)
      throws SQLException {

    connection.setAutoCommit(autoCommit);

    StringBuffer command = new StringBuffer();
    try {
      for(String line: lines) {
        String trimmedLine = line.trim();

        if(trimmedLine.length() < 1 || trimmedLine.startsWith("//") || trimmedLine.startsWith("--"))
          // Comment or empty line - do nothing
          continue;

        if(trimmedLine.endsWith(DELIMITER) || trimmedLine.equals(DELIMITER)) {
          // Statement ends, execute
          command.append(line.substring(0, line.lastIndexOf(DELIMITER)) + " ");
          Statement statement = connection.createStatement();

          if(stopOnError) {
            statement.execute(command.toString());
          } else {
            try {
              statement.execute(command.toString());
            } catch (SQLException e) {
              e.fillInStackTrace();
              theLogger.error("Error executing: " + command, e);
            }
          }

          if(autoCommit && !connection.getAutoCommit()) {
            connection.commit();
          }

          command = new StringBuffer();
          statement.close();
          Thread.yield();
          
        } else {
          command.append(line + " ");
        }
      }
      if(!autoCommit) {
        connection.commit();
      }
    } catch (SQLException e) {
      e.fillInStackTrace();
      theLogger.error("Error executing: " + command, e);
    } finally {
      connection.rollback();
    }
  }

  private ScriptRunner() {

  }

}