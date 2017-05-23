package controllers.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import model.Secured;
import model.logging.WorkingEvent;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Security.Authenticated;

@Authenticated(Secured.class)
public abstract class ExerciseController extends AbstractController {

  private static final Logger.ALogger PROGRESS_LOGGER = Logger.of("progress");

  public ExerciseController(FormFactory theFactory, String theExerciseType) {
    super(theFactory, theExerciseType);
  }

  protected static Path getSolFileForExercise(User user, String exerciseType, int exercise, String fileType) {
    return Paths.get(BASE_DATA_PATH, SOLUTIONS_SUB_DIRECTORY, user.name, exerciseType, exercise + "." + fileType);
  }

  protected static void log(User user, WorkingEvent eventToLog) {
    PROGRESS_LOGGER.debug(user.name + " - " + Json.toJson(eventToLog));
  }

  protected Path checkAndCreateSolDir(User user, String exerciseType, int id) {
    Path dir = Paths.get(getSolDirForUser().toString(), exerciseType, Integer.toString(id));

    if(dir.toFile().exists())
      return dir;

    try {
      return Files.createDirectories(dir);
    } catch (IOException e) {
      Logger.error("There was an error while creating the directory for an " + exerciseType + " solution: " + dir, e);
      return null;
    }
  }

  protected boolean wantsJsonResponse() {
    return "application/json".equals(request().acceptedTypes().get(0).toString());
  }

}
