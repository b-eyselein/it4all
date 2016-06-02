package controllers.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Singleton;

import play.Configuration;
import play.Environment;
import play.Logger;

@Singleton
public class Util {
  
  private static Logger.ALogger theLogger = Logger.of("startup");
  private Path rootSolDir;

  private String serverUrl;

  private Environment environment;
  private Configuration configuration;

  @Inject
  public Util(Configuration theConfiguration, Environment theEnvironment) {
    environment = theEnvironment;
    configuration = theConfiguration;
    readRootDir();
    readServerUrl();

    if(!Files.exists(rootSolDir))
      theLogger.error("Folder for solutions does not exits!");
    
  }

  public Path getExcelSampleDirectoryForExercise(String exerciseType, int exerciseId) {
    return Paths.get(rootSolDir.toString(), "samples", exerciseType, "ex_" + exerciseId);
  }

  public Path getExcelSolFileForExercise(String user, String fileName) {
    return Paths.get(getSolDirForUserAndType("excel", user).toString(), fileName);
  }

  public Path getRootSolDir() {
    return rootSolDir;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public Path getSolDirForUser(String user) {
    return Paths.get(rootSolDir.toString(), "solutions", user);
  }

  public Path getSolDirForUserAndType(String type, String user) {
    return Paths.get(getSolDirForUser(user).toString(), type);
  }

  public Path getSolutionFileForExerciseAndType(String user, String exerciseType, int exercise) {
    return Paths.get(getSolDirForUserAndType(exerciseType, user).toString(), exercise + ".html");
  }

  public Path getXmlReferenceFilePath(String referenceFileName) {
    return Paths.get(rootSolDir.toString(), "samples", "xml", referenceFileName);
  }

  public Path getXmlSolFileForExercise(String user, int exerciseId) {
    // TODO: Test
    return Paths.get(getSolDirForUserAndType("xml", user).toString(), exerciseId + "");
  }

  public void readServerUrl() {
    if(environment.isDev() || environment.isTest())
      serverUrl = "http://localhost:9000";
    else if(environment.isProd())
      serverUrl = "https://www.it4all.uni-wuerzburg.de";
    else
      throw new IllegalArgumentException("Cound not determine URL of Server!");
  }

  private void readRootDir() {
    String os = System.getProperty("os.name").toLowerCase();

    // WINDOWS
    if(os.indexOf("win") >= 0)
      rootSolDir = Paths.get(configuration.getString("rootDirWin"));
    // UNIX
    else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0)
      rootSolDir = Paths.get(configuration.getString("rootDirLinux"));
    // OTHER OS, NEEDS CONFIGURATION
    else
      throw new IllegalArgumentException("OS not detectable");
  }

}
