package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Singleton;

import model.user.User;
import play.Configuration;
import play.Logger;

@Singleton
public class Util {
  
  private static final String SAMPLE_SUB_DIRECTORY = "samples";
  private static final String SOLUTIONS_SUB_DIRECTORY = "solutions";
  private static final Logger.ALogger theLogger = Logger.of("startup");

  private Path rootSolDir;

  private Configuration configuration;

  @Inject
  public Util(Configuration theConfiguration) {
    configuration = theConfiguration;

    readRootDir();

    if(!Files.exists(rootSolDir)) {
      theLogger.error("Folder for solutions does not exits. Trying to create it...");
      try {
        Files.createDirectories(rootSolDir);
      } catch (IOException e) {
        theLogger.error("Could not create folder for solutions!", e);
      }
    }

    try {
      String fileOwner = Files.getOwner(rootSolDir).getName();
      String processOwner = System.getProperty("user.name");
      if(!fileOwner.equals(processOwner))
        theLogger.error(
            "FileOwner of folder for solutions " + fileOwner + " does not match starter of programm " + processOwner);
    } catch (IOException e) {
    }
  }

  public Path getRootSolDir() {
    return rootSolDir;
  }

  public Path getSampleDirForExercise(String exerciseType) {
    return Paths.get(rootSolDir.toString(), SAMPLE_SUB_DIRECTORY, exerciseType);
  }

  public Path getSampleFileForExercise(String exerciseType, String fileName) {
    return Paths.get(getSampleDirForExercise(exerciseType).toString(), fileName);
  }

  public Path getSoFileForExercise(User user, String exerciseType, String fileName) {
    return Paths.get(getSolDirForUserAndType(user, exerciseType).toString(), fileName);
  }

  public Path getSolDirForUser(User user) {
    return Paths.get(rootSolDir.toString(), SOLUTIONS_SUB_DIRECTORY, user.name);
  }

  public Path getSolDirForUserAndType(User user, String exerciseType) {
    return Paths.get(getSolDirForUser(user).toString(), exerciseType);
  }

  public Path getSolFileForExerciseAndType(User user, String exerciseType, int exercise, String fileType) {
    return Paths.get(getSolDirForUserAndType(user, exerciseType).toString(), exercise + "." + fileType);
  }

  public Path getSolFileForExerciseAndType(User user, String exerciseType, String exercise, String fileType) {
    return Paths.get(getSolDirForUserAndType(user, exerciseType).toString(), exercise + "." + fileType);
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
