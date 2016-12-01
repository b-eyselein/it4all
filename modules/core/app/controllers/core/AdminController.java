package controllers.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import model.AdminSecured;
import model.Util;
import model.exercise.Exercise;
import model.exercisereading.ExerciseReader;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

@Authenticated(AdminSecured.class)
public abstract class AdminController<E extends Exercise, R extends ExerciseReader<E>> extends Controller {
  
  protected static final String BODY_FILE_NAME = "file";
  protected static final String ADMIN_FOLDER = "admin";
  
  protected R exerciseReader;

  protected String exerciseType;
  
  protected Util util;
  
  public AdminController(Util theUtil, String theExerciseType, R theExerciseReader) {
    util = theUtil;
    exerciseType = theExerciseType;
    exerciseReader = theExerciseReader;
  }
  
  protected static void saveUploadedFile(Path savingDir, Path pathToUploadedFile, Path saveTo) {
    try {
      if(!savingDir.toFile().exists() && !savingDir.toFile().isDirectory())
        Files.createDirectories(savingDir);
      Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      Logger.error("Error while saving uploaded sql file!", e);
    }
  }
  
  // FIXME: all following methods which return Result are more or less equal!
  
  public abstract Result create();
  
  protected abstract void saveExercises(List<E> exercises);
  
  public abstract Result uploadFile();
  
  public abstract Result uploadForm();
}
