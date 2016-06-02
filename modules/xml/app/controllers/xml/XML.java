package controllers.xml;

import java.io.File;
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
import model.Util;
import model.XMLError;
import model.XmlCorrector;
import model.XmlErrorType;
import model.user.Secured;
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
  Util util;

  public Result commit(int exerciseId) {
    User user = UserManagement.getCurrentUser();

    String learnerSolution = extractLearnerSolutionFromRequest(request());
    Logger.info(learnerSolution);
    Path path2solution = saveSolutionForUser(user, learnerSolution, exerciseId);

    List<XMLError> elementResults = correctExercise(path2solution, user, XmlExercise.finder.byId(exerciseId));

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
      // TODO: bestimme fileType ("xml", "xsd", "dtd")
      String fileType = "xml";
      Path oldSolutionPath = util.getSolutionFileForExerciseAndType(user, EXERCISE_TYPE, exerciseId, fileType);
      if(Files.exists(oldSolutionPath, LinkOption.NOFOLLOW_LINKS))
        defaultOrOldSolution = String.join("\n", Files.readAllLines(oldSolutionPath));
      
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }

    String referenceCode = "";
    try {
      Path referenceFilePath = util.getXmlReferenceFilePath(exercise.referenceFileName);
      Logger.debug(referenceFilePath.toString());
      if(Files.exists(referenceFilePath, LinkOption.NOFOLLOW_LINKS))
        referenceCode = String.join("\n", Files.readAllLines(referenceFilePath));
      
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }

    return ok(xml.render(UserManagement.getCurrentUser(), exercise, referenceCode, defaultOrOldSolution,
        util.getServerUrl()));
  }

  public Result index() {
    return ok(xmloverview.render(XmlExercise.finder.all(), UserManagement.getCurrentUser()));
  }

  private List<XMLError> correctExercise(Path solutionPath, User user, XmlExercise exercise) {
    File solutionFile = new File(solutionPath.toString());
    File referenceFile = new File(util.getXmlReferenceFilePath(exercise.referenceFileName).toString());
    List<XMLError> result = null;
    try {
      result = XmlCorrector.correct(solutionFile, referenceFile, exercise, user);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if(result.isEmpty()) {
      result.add(new XMLError(XmlErrorType.NONE, "Super!", "Bist ein ganz guter Student."));
    }
    boolean malformed = false;
    for(XMLError el: result) {
      // TODO: passt hier pruefen auf FATALERROR oder vielleicht doch lieber
      // "el.getErrorType() != XmlErrorType.NONE"
      if(el.getErrorType() == XmlErrorType.FATALERROR)
        malformed = true;
    }
    if(!result.isEmpty() && !malformed) {
      result.add(new XMLError(XmlErrorType.NONE, "Die Eingabe ist wohlgeformt", ""));
    }
    return result;
  }

  private String extractLearnerSolutionFromRequest(Request request) {
    return request.body().asFormUrlEncoded().get(LEARNER_SOLUTION_VALUE)[0];
  }

  private Path saveSolutionForUser(User user, String solution, int exercise) {
    try {
      Path solDir = util.getSolDirForUserAndType(user, EXERCISE_TYPE);
      if(!Files.exists(solDir))
        Files.createDirectories(solDir);
      
      // TODO: bestimme fileType ("xml", "xsd", "dtd")
      String fileType = "xml";
      Path saveTo = util.getSolutionFileForExerciseAndType(user, EXERCISE_TYPE, exercise, fileType);
      Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      return saveTo;
    } catch (IOException error) {
      Logger.error("Fehler beim Speichern einer Xml-Loesungsdatei!", error);
    }
    return null;
  }

}
