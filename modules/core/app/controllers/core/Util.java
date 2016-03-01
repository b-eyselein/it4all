package controllers.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import play.Configuration;

public class Util {
  
  // Load root directory for solutions and samples at startUp
  private static String rootSolDir = getRootDir();
  
  private static String getRootDir() {
    String os = System.getProperty("os.name").toLowerCase();
    
    // WINDOWS
    if(os.indexOf("win") >= 0)
      return Configuration.root().getString("rootDirWin");
    // UNIX
    else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0)
      return Configuration.root().getString("rootDirLinux");
    // OTHER OS, NEEDS CONFIGURATION
    else
      throw new IllegalArgumentException("OS not detectable");
  }
  
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
