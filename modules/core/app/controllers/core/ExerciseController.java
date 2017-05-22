package controllers.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import model.Secured;
import model.Util;
import model.logging.WorkingEvent;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Security.Authenticated;

@Authenticated(Secured.class)
public abstract class ExerciseController extends AbstractController {
  
  private static final Logger.ALogger PROGRESS_LOGGER = Logger.of("progress");
  
  public ExerciseController(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  protected static void log(User user, WorkingEvent eventToLog) {
    PROGRESS_LOGGER.debug(user.name + " - " + Json.toJson(eventToLog));
  }
  
  protected Path checkAndCreateSolDir(User user, String exerciseType, int id) {
    Path dir = Paths.get(util.getSolDirForUserAndType(user, exerciseType).toString(), Integer.toString(id));

    if(dir.toFile().exists())
      return dir;
    
    try {
      return Files.createDirectories(dir);
    } catch (IOException e) {
      Logger.error("There was an error while creating the directory for the xml solution " + dir, e);
      return null;
    }
  }
  
  protected boolean wantsJsonResponse() {
    return "application/json".equals(request().acceptedTypes().get(0).toString());
  }
  
}
