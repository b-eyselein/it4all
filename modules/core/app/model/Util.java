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
  
  private Path rootDirForFiles;
  
  private Configuration configuration;
  
  @Inject
  public Util(Configuration theConfiguration) throws IOException {
    configuration = theConfiguration;
    
    rootDirForFiles = readRootDirFromConfig();
    
    if(rootDirForFiles.toFile().exists())
      return;
    
    theLogger.error("Folder for storing of samples and solutions does not exits. Trying to create it...");
    try {
      Files.createDirectories(rootDirForFiles);
      
      String fileOwner = Files.getOwner(rootDirForFiles).getName();
      String processOwner = System.getProperty("user.name");
      
      if(!fileOwner.equals(processOwner))
        theLogger.error(
            "FileOwner of folder for solutions " + fileOwner + " does not match starter of programm " + processOwner);
    } catch (IOException e) {
      throw new IOException("Could not create folder for samples and solutions " + rootDirForFiles, e);
    }
    
  }

  public static String readFileFromPath(Path oldSolutionPath) throws IOException {
    return String.join("\n", Files.readAllLines(oldSolutionPath));
  }
  
  public Path getRootSolDir() {
    return rootDirForFiles;
  }
  
  public Path getSampleDirForExercise(String exerciseType, int exerciseId) {
    return Paths.get(getSampleDirForExerciseType(exerciseType).toString(), Integer.toString(exerciseId));
  }
  
  public Path getSampleDirForExerciseType(String exerciseType) {
    return Paths.get(rootDirForFiles.toString(), SAMPLE_SUB_DIRECTORY, exerciseType);
  }
  
  public Path getSampleFileForExercise(String exerciseType, String fileName) {
    return Paths.get(getSampleDirForExerciseType(exerciseType).toString(), fileName);
  }
  
  public Path getSolDirForUser(User user) {
    return Paths.get(rootDirForFiles.toString(), SOLUTIONS_SUB_DIRECTORY, user.name);
  }
  
  public Path getSolDirForUserAndType(User user, String exerciseType) {
    return Paths.get(getSolDirForUser(user).toString(), exerciseType);
  }
  
  public Path getSolFileForExercise(User user, String exerciseType, int exercise, String fileType) {
    return getSolFileForExercise(user, exerciseType, exercise + "." + fileType);
  }
  
  public Path getSolFileForExercise(User user, String exerciseType, String fileName) {
    return Paths.get(getSolDirForUserAndType(user, exerciseType).toString(), fileName);
  }
  
  public Path getSolFileForExercise(User user, String exerciseType, String exerciseName, String fileType) {
    return getSolFileForExercise(user, exerciseType, exerciseName + "." + fileType);
  }
  
  private Path readRootDirFromConfig() {
    String os = System.getProperty("os.name").toLowerCase();
    
    // WINDOWS
    if(os.indexOf("win") >= 0)
      return Paths.get(configuration.getString("rootDirWin"));
    // UNIX
    else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0)
      return Paths.get(configuration.getString("rootDirLinux"));
    // OTHER OS, NEEDS CONFIGURATION
    else
      throw new IllegalArgumentException("OS not detectable");
  }
  
}
