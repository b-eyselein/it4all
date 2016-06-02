package model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Singleton;

import model.user.User;
import play.Configuration;
import play.Environment;
import play.Logger;

@Singleton
public class Util {
  
  private static final String SAMPLE_SUB_DIRECTORY = "samples";

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

  public Path getExcelSolFileForExercise(User user, String fileName, String exerciseType) {
    return Paths.get(getSolDirForUserAndType(user, exerciseType).toString(), fileName);
  }

  public Path getRootSolDir() {
    return rootSolDir;
  }

  public Path getSampleDirectoryForExercise(String exerciseType, int exerciseId) {
    return Paths.get(rootSolDir.toString(), SAMPLE_SUB_DIRECTORY, exerciseType, "ex_" + exerciseId);
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public Path getSolDirForUser(User user) {
    return Paths.get(rootSolDir.toString(), "solutions", user.name);
  }

  public Path getSolDirForUserAndType(User user, String exerciseType) {
    return Paths.get(getSolDirForUser(user).toString(), exerciseType);
  }

  public Path getSolutionFileForExerciseAndType(User user, String exerciseType, int exercise, String fileType) {
    return Paths.get(getSolDirForUserAndType(user, exerciseType).toString(), exercise + "." + fileType);
  }

  public Path getXmlReferenceFilePath(String referenceFileName) {
    return Paths.get(rootSolDir.toString(), SAMPLE_SUB_DIRECTORY, "xml", referenceFileName);
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
