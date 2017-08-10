package controllers.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import io.ebean.Finder;
import model.StringConsts;
import model.WithId;
import model.exercise.Exercise;
import model.logging.WorkingEvent;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;

public abstract class BaseController extends Controller {

  protected static final String BASE_DATA_PATH = "/data"; // NOSONAR

  protected static final String SAMPLE_SUB_DIRECTORY = "samples";
  protected static final String SOLUTIONS_SUB_DIRECTORY = "solutions";

  private static final Logger.ALogger PROGRESS_LOGGER = Logger.of("progress");

  protected FormFactory factory;

  public BaseController(FormFactory theFactory) {
    factory = theFactory;
  }

  public static Path getSolDirForUser(String username) {
    return Paths.get(BASE_DATA_PATH, SOLUTIONS_SUB_DIRECTORY, username);
  }

  public static User getUser() {
    return User.finder.byId(getUsername());
  }

  protected static <T extends WithId> int findMinimalNotUsedId(Finder<Integer, T> finder) {
    // FIXME: this is probably a ugly hack...
    List<T> questions = finder.all();

    Collections.sort(questions);

    if(questions.isEmpty())
      return 1;

    for(int i = 0; i < questions.size() - 1; i++)
      if(questions.get(i).getId() < questions.get(i + 1).getId() - 1)
        return questions.get(i).getId() + 1;

    return questions.get(questions.size() - 1).getId() + 1;
  }

  protected static Path getSampleDir(String exerciseType) {
    return Paths.get(BASE_DATA_PATH, SAMPLE_SUB_DIRECTORY, exerciseType);
  }

  protected static Path getSampleDirForExercise(String exerciseType, Exercise exercise) {
    return Paths.get(getSampleDir(exerciseType).toString(), String.valueOf(exercise.getId()));
  }

  protected static Path getSolDirForExercise(String username, String exerciseType, Exercise exercise) {
    return Paths.get(getSolDirForUser(username).toString(), exerciseType, String.valueOf(exercise.getId()));
  }

  protected static Path getSolFileForExercise(String username, String exerciseType, Exercise exercise, String fileName,
      String fileExtension) {
    return Paths.get(getSolDirForExercise(username, exerciseType, exercise).toString(), fileName + "." + fileExtension);
  }

  protected static String getUsername() {
    Http.Session session = Http.Context.current().session();

    if(session == null || session.get(StringConsts.SESSION_ID_FIELD) == null
        || session.get(StringConsts.SESSION_ID_FIELD).isEmpty())
      throw new IllegalArgumentException("No user name was given!");

    return session.get(StringConsts.SESSION_ID_FIELD);
  }

  protected static void log(User user, WorkingEvent eventToLog) {
    PROGRESS_LOGGER.debug(user.name + " - " + Json.toJson(eventToLog));
  }

  public Path getSolDirForUser() {
    return getSolDirForUser(getUsername());
  }

}
