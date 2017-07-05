package controllers.core;

import java.nio.file.Path;
import java.nio.file.Paths;

import model.exercise.Exercise;
import model.logging.WorkingEvent;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;

public abstract class BaseExerciseController extends BaseController {

  private static final Logger.ALogger PROGRESS_LOGGER = Logger.of("progress");

  protected String exerciseType;

  public BaseExerciseController(FormFactory theFactory, String theExerciseType) {
    super(theFactory);
    exerciseType = theExerciseType;
  }

  protected static void log(User user, WorkingEvent eventToLog) {
    PROGRESS_LOGGER.debug(user.name + " - " + Json.toJson(eventToLog));
  }

  protected Path getSampleDir() {
    return Paths.get(BASE_DATA_PATH, SAMPLE_SUB_DIRECTORY, exerciseType);
  }

  protected Path getSampleDirForExercise(Exercise exercise) {
    return Paths.get(getSampleDir().toString(), String.valueOf(exercise.id));
  }

  protected Path getSolDirForExercise(Exercise exercise) {
    return Paths.get(getSolDirForUser().toString(), exerciseType, String.valueOf(exercise.id));
  }

  protected Path getSolFileForExercise(Exercise exercise, String fileName, String fileExtension) {
    return Paths.get(getSolDirForExercise(exercise).toString(), fileName + "." + fileExtension);
  }

}
