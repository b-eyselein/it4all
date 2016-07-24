package model;
/*
 * Slightly modified version of the com.ibatis.common.jdbc.ScriptRunner class
 * from the iBATIS Apache project. Only removed dependency on Resource class
 * and a constructor
 */

/*
 *  Copyright 2004 Clinton Begin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import play.Logger;

/**
 * Tool to run database scripts
 */
public class ScriptRunner {
  
  private static final Logger.ALogger theLogger = Logger.of("sql");
  private static final String DELIMITER = ";";

  /**
   * Runs an SQL script (read in using the Reader parameter)
   *
   * @param reader
   *          - the source of the script
   */
  public static void runScript(Connection connection, Reader reader, boolean autoCommit, boolean stopOnError)
      throws IOException, SQLException {
    try {
      if(connection.getAutoCommit() != autoCommit)
        connection.setAutoCommit(autoCommit);
      
      runScriptReally(connection, reader, autoCommit, stopOnError);
    } catch (IOException | SQLException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error running script.  Cause: " + e, e);
    }
  }

  /**
   * Runs an SQL script (read in using the Reader parameter) using the
   * connection passed in
   *
   * @param conn
   *          - the connection to use for the script
   * @param reader
   *          - the source of the script
   * @throws SQLException
   *           if any SQL errors occur
   * @throws IOException
   *           if there is an error reading from the Reader
   */
  private static void runScriptReally(Connection conn, Reader reader, boolean autoCommit, boolean stopOnError)
      throws IOException, SQLException {
    StringBuffer command = null;
    try {
      LineNumberReader lineReader = new LineNumberReader(reader);
      String line = null;
      while((line = lineReader.readLine()) != null) {
        if(command == null) {
          command = new StringBuffer();
        }
        String trimmedLine = line.trim();
        if(trimmedLine.length() < 1 || trimmedLine.startsWith("//") || trimmedLine.startsWith("--")) {
          // Comment or empty line - do nothing
        } else if(trimmedLine.endsWith(DELIMITER) || trimmedLine.equals(DELIMITER)) {
          // Statement ends, execute
          command.append(line.substring(0, line.lastIndexOf(DELIMITER)) + " ");
          Statement statement = conn.createStatement();

          theLogger.info(command + "");

          boolean hasResults = false;
          if(stopOnError) {
            hasResults = statement.execute(command.toString());
          } else {
            try {
              statement.execute(command.toString());
            } catch (SQLException e) {
              e.fillInStackTrace();
              theLogger.error("Error executing: " + command, e);
            }
          }

          if(autoCommit && !conn.getAutoCommit()) {
            conn.commit();
          }

          ResultSet rs = statement.getResultSet();
          if(hasResults && rs != null) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            for(int i = 0; i < cols; i++) {
              String name = md.getColumnLabel(i);
              theLogger.info(name + "\n");
            }
            while(rs.next()) {
              for(int i = 0; i < cols; i++) {
                String value = rs.getString(i);
                theLogger.info(value + "\n");
              }
            }
          }

          command = null;
          try {
            statement.close();
          } catch (Exception e) {
            // Ignore to workaround a bug in Jakarta DBCP
          }
          Thread.yield();
        } else {
          command.append(line + " ");
        }
      }
      if(!autoCommit) {
        conn.commit();
      }
    } catch (SQLException | IOException e) {
      e.fillInStackTrace();
      theLogger.error("Error executing: " + command, e);
      throw e;
    } finally {
      conn.rollback();
    }
  }

}