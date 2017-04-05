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
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

@Authenticated(AdminSecured.class)
public abstract class AbstractAdminController<E extends Exercise, R extends ExerciseReader<E>> extends AbstractController {
  
  protected static final String BODY_FILE_NAME = "file";
  protected static final String ADMIN_FOLDER = "admin";
  
  protected R exerciseReader;
  
  protected String exerciseType;
  
  public AbstractAdminController(Util theUtil, FormFactory theFactory, String theExerciseType, R theExerciseReader) {
    super(theUtil, theFactory);
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
  
  public abstract Result readStandardExercises();
  
  public abstract Result uploadFile();
  
  public abstract Result uploadForm();
  
  protected abstract void saveExercises(List<E> exercises);
}
