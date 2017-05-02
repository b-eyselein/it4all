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
public final class ScriptRunner {

  private static final Logger.ALogger THE_LOGGER = Logger.of("sql");

  private static final String DELIMITER = ";";

  private ScriptRunner() {

  }

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

    StringBuilder command = new StringBuilder();
    try {
      for(final String line: lines) {
        final String trimmedLine = line.trim();

        if(isEmptyOrCommentLine(trimmedLine))
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
              THE_LOGGER.error("Error executing: " + command, e);
            }
          }

          if(autoCommit && !connection.getAutoCommit()) {
            connection.commit();
          }

          command = new StringBuilder();
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
      THE_LOGGER.error("Error executing: " + command, e);
    } finally {
      connection.rollback();
    }
  }

  private static boolean isEmptyOrCommentLine(final String trimmedLine) {
    return trimmedLine.isEmpty() || trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
  }

}