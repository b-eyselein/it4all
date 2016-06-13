package controllers.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import model.XmlExercise;
import controllers.core.UserManagement;
import model.ExerciseType;
import model.Secured;
import model.Util;
import model.XMLError;
import model.XmlCorrector;
import model.XmlErrorType;
import model.user.User;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.xml;
import views.html.xmloverview;
import views.html.xmlcorrect;

@Security.Authenticated(Secured.class)
public class XML extends Controller {
  
  private static final String EXERCISE_TYPE = "xml";
  private static final String LEARNER_SOLUTION_VALUE = "editorContent";
  private static final String STANDARD_XML = "";

  @Inject
  private Util util;

  @Inject
  @SuppressWarnings("unused")
  private XmlStartUpChecker checker;

  public Result commit(int exerciseId) {
    User user = UserManagement.getCurrentUser();
    XmlExercise exercise = XmlExercise.finder.byId(exerciseId);

    String learnerSolution = extractLearnerSolutionFromRequest(request());
    // Logger.info(learnerSolution);
    Path path2solution = saveSolutionForUser(user, learnerSolution, exercise);

    List<XMLError> elementResults = correctExercise(path2solution, user, exercise);

    for(XMLError error: elementResults)
      Logger.debug(error.toString());
    
    if(request().acceptedTypes().get(0).toString().equals("application/json"))
      return ok(Json.toJson(elementResults));
    else
      return ok(xmlcorrect.render(learnerSolution, elementResults, UserManagement.getCurrentUser()));
  }

  public Result exercise(int exerciseId) {
    XmlExercise exercise = XmlExercise.finder.byId(exerciseId);

    if(exercise == null)
      return badRequest(new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
          + routes.XML.index() + "\">Startseite</a>.</p>"));
    
    User user = UserManagement.getCurrentUser();
    String defaultOrOldSolution = STANDARD_XML;
    try {
      Path oldSolutionPath = util.getSolutionFileForExerciseAndType(user, EXERCISE_TYPE, exerciseId,
          exercise.exerciseType.studentFileEnding);
      if(Files.exists(oldSolutionPath, LinkOption.NOFOLLOW_LINKS))
        defaultOrOldSolution = String.join("\n", Files.readAllLines(oldSolutionPath));
      
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }

    String referenceCode = "";
    try {
      Path referenceFilePath = util.getSampleFileForExerciseAndType(EXERCISE_TYPE, exercise.referenceFileName);
      if(Files.exists(referenceFilePath, LinkOption.NOFOLLOW_LINKS))
        referenceCode = String.join("\n", Files.readAllLines(referenceFilePath));
      
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }

    return ok(xml.render(UserManagement.getCurrentUser(), exercise, referenceCode, defaultOrOldSolution));
  }

  public Result index() {
    return ok(xmloverview.render(XmlExercise.finder.all(), UserManagement.getCurrentUser()));
  }

  private List<XMLError> correctExercise(Path solutionPath, User user, XmlExercise exercise) {
    Path learnerSolution = solutionPath;
    Path referenceFile = null;
    switch(exercise.exerciseType) {
    case XMLAgainstDTD:
    case XMLAgainstXSD:
      referenceFile = util.getSampleFileForExerciseAndType(EXERCISE_TYPE, exercise.referenceFileName);
      break;
    case DTDAgainstXML:
      referenceFile = createCustomReferenceFileforUser(solutionPath, user, exercise);
      break;
    default:
      return null;
    }

    List<XMLError> result = null;
    Logger.info(exercise.exerciseType.toString());
    try {
      result = XmlCorrector.correct(learnerSolution.toFile(), referenceFile.toFile(), exercise, user);
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }
    
    if(result.isEmpty()) {
      result.add(new XMLError(XmlErrorType.NONE, "Alles richtig!", ""));
    }
    boolean malformed = false;
    for(XMLError el: result) {
      if(el.getErrorType() == XmlErrorType.FATALERROR || el.getErrorType() == XmlErrorType.ERROR)
        malformed = true;
    }
    if(!result.isEmpty() && !malformed) {
      result.add(new XMLError(XmlErrorType.NONE, "Die Eingabe ist wohlgeformt.", ""));
    }
    return result;
  }

  private Path createCustomReferenceFileforUser(Path solutionPath, User user, XmlExercise exercise) {
    Path result = util.getSolutionFileForExerciseAndType(user, EXERCISE_TYPE, "reference_for_" + exercise.id, "xml");
    String content = "";
    try {
      content = "<?xml version=\"1.0\" ?>\n" + "<!DOCTYPE party SYSTEM \"" + solutionPath.toString() + "\">\n"
          + String.join("\n",
              Files.readAllLines(util.getSampleFileForExerciseAndType(EXERCISE_TYPE, exercise.referenceFileName)))
          + "\n";

      Files.write(result, Arrays.asList(content), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      Logger.error("Fehler beim Lesen oder Schreiben einer XML-Referenzdatei.");
    }
    // Logger.info(content);
    return result;
  }

  private String extractLearnerSolutionFromRequest(Request request) {
    return request.body().asFormUrlEncoded().get(LEARNER_SOLUTION_VALUE)[0];
  }

  private Path saveSolutionForUser(User user, String solution, XmlExercise exercise) {
    try {
      Path solDir = util.getSolDirForUserAndType(user, EXERCISE_TYPE);
      if(!Files.exists(solDir))
        Files.createDirectories(solDir);
      
      Path saveTo = util.getSolutionFileForExerciseAndType(user, EXERCISE_TYPE, exercise.id,
          exercise.exerciseType.studentFileEnding);
      Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      return saveTo;
    } catch (IOException error) {
      Logger.error("Fehler beim Speichern einer Xml-Loesungsdatei!", error);
    }
    return null;
  }

}
