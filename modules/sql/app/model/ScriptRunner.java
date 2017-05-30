package model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import play.Logger;

public final class ScriptRunner {
  
  private static final String DELIMITER = ";";
  
  private static final String COMMENT_SIGN_1 = "//";
  private static final String COMMENT_SIGN_2 = "--";
  
  public static void runScript(Connection connection, List<String> lines) {
    
    StringBuilder command = new StringBuilder();
    try {
      for(final String line: lines) {
        final String trimmedLine = line.trim();
        
        if(isEmptyOrCommentLine(trimmedLine))
          // Comment or empty line - do nothing
          continue;
        
        if(!trimmedLine.endsWith(DELIMITER) || !trimmedLine.equals(DELIMITER)) {
          command.append(line + " ");
          continue;
        }
        
        // Statement ends, execute
        command.append(line.substring(0, line.lastIndexOf(DELIMITER)) + " ");
        
        runStatement(connection, command.toString());
        
        connection.commit();
        
        command = new StringBuilder();
        Thread.yield();
      }
    } catch (SQLException e) {
      Logger.error("Error while executing command: " + command, e);
    }
  }
  
  private static boolean isEmptyOrCommentLine(final String trimmedLine) {
    return trimmedLine.isEmpty() || trimmedLine.startsWith(COMMENT_SIGN_1) || trimmedLine.startsWith(COMMENT_SIGN_2);
  }
  
  private static void runStatement(Connection connection, String command) {
    try(Statement statement = connection.createStatement();) {
      statement.execute(command);
    } catch (SQLException e) {
      Logger.error("Error executing: " + command, e);
    }
  }
  
  private ScriptRunner() {
    
  }
  
}