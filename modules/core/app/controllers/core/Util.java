package controllers.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Singleton;

import play.Configuration;
import play.Environment;

@Singleton
public class Util {
  
  // Load root directory for solutions and samples at startUp
  private String rootSolDir;

  private Environment environment;

  private Configuration configuration;

  @Inject
  public Util(Configuration theConfiguration, Environment theEnvironment) {
    environment = theEnvironment;
    configuration = theConfiguration;
    rootSolDir = getRootDir();
  }

  public Path getExcelSampleDirectoryForExercise(String exerciseType, int exerciseId) {
    return Paths.get(rootSolDir, "samples", exerciseType, "ex_" + exerciseId);
  }

  public Path getExcelSolFileForExercise(String user, String fileName) {
    return Paths.get(getSolDirForUserAndType("excel", user).toString(), fileName);
  }

  public Path getHtmlSolFileForExercise(String user, String exerciseType, int exercise) {
    // TODO: Test for Html!
    return Paths.get(getSolDirForUserAndType("html", user).toString(), exercise + ".html");
  }

  public Path getRootSolDir() {
    return Paths.get(rootSolDir);
  }

  public String getServerUrl() {
    if(environment.isDev() || environment.isTest())
      return "http://localhost:9000";
    else if(environment.isProd())
      return "https://www.it4all.uni-wuerzburg.de";
    else
      throw new IllegalArgumentException("Cound not determine Upload-URL for JS-Testing!");
  }

  public Path getSolDirForUser(String user) {
    return Paths.get(rootSolDir, "solutions", user);
  }

  public Path getSolDirForUserAndType(String type, String user) {
    return Paths.get(getSolDirForUser(user).toString(), type);
  }

  public Path getXmlReferenceFilePath(String referenceFileName) {
    return Paths.get(rootSolDir, "references", "xml", referenceFileName);
  }

  public Path getXmlSolFileForExercise(String user, int exerciseId) {
    // TODO: Test
    return Paths.get(getSolDirForUserAndType("xml", user).toString(), exerciseId + "");
  }

  private String getRootDir() {
    String os = System.getProperty("os.name").toLowerCase();

    // WINDOWS
    if(os.indexOf("win") >= 0)
      return configuration.getString("rootDirWin");
    // UNIX
    else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0)
      return configuration.getString("rootDirLinux");
    // OTHER OS, NEEDS CONFIGURATION
    else
      throw new IllegalArgumentException("OS not detectable");
  }

}
