package controllers.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import play.Configuration;

public class Util {

  // Load root directory for solutions and samples at startUp
  private static String rootSolDir = Configuration.root().getString("rootDir");
  
  public static Path getSolDirForUser(String user) {
    return Paths.get(rootSolDir, "solutions", user);
  }
  
  public static Path getSolDirForUserAndType(String type, String user) {
    return Paths.get(getSolDirForUser(user).toString(), type);
  }
  
  public static Path getHtmlSolFileForExercise(String user, String exerciseType, int exercise) {
    // TODO: Test for Html!
    return Paths.get(getSolDirForUserAndType("html", user).toString(), exercise + ".html");
  }
  
  public static Path getExcelSampleDirectoryForExercise(int exerciseId) {
    return Paths.get(rootSolDir, "samples", "excel", "ex_" + exerciseId);
  }
  
  public static Path getExcelSolFileForExercise(String user, String fileName) {
    return Paths.get(Util.getSolDirForUserAndType("excel", user).toString(), fileName);
  }
  
}
