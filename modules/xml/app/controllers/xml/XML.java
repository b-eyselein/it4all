package controllers.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import model.XmlExercise;
import model.ElementResult;
import model.XmlCorrector;
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
import controllers.core.UserControl;
import controllers.core.Util;
//import controllers.xml.;

//@Security.Authenticated(Secured.class)
public class XML extends Controller {
  
  private static final String SERVER_URL = Util.getServerUrl();
  private static final String LEARNER_SOLUTION_VALUE = "editorContent";
  private static final String STANDARD_XML = "";

  
  @Security.Authenticated(Secured.class)
  public Result index() {
    return ok(xmloverview.render(XmlExercise.finder.all(), UserControl.getUser()));
  }
  
  @Security.Authenticated(Secured.class)
  public Result exercise(int exerciseId) {
    XmlExercise exercise = XmlExercise.finder.byId(exerciseId);

    if(exercise == null)
      return badRequest(new Html("<p>Diese Aufgabe existert leider nicht.</p><p>Zur&uuml;ck zur <a href=\""
          + routes.XML.index() + "\">Startseite</a>.</p>"));
    
    User user = UserControl.getUser();
	String defaultOrOldSolution = STANDARD_XML;
    try {
      Path oldSolutionPath = Util.getXmlSolFileForExercise(user.getName(), exerciseId);
      if(Files.exists(oldSolutionPath, LinkOption.NOFOLLOW_LINKS))
        defaultOrOldSolution = String.join("\n", Files.readAllLines(oldSolutionPath));

    } catch (IOException e) {
      Logger.error(e.getMessage());
    }
	
    return ok(xml.render(UserControl.getUser(), exercise, "TODO", defaultOrOldSolution, SERVER_URL));
  }
  
  @Security.Authenticated(Secured.class)
  public Result commit(int exerciseId) {
    User user = UserControl.getUser();

    String learnerSolution = extractLearnerSolutionFromRequest(request());
    saveSolutionForUser(user.getName(), learnerSolution, exerciseId);

    List<ElementResult> elementResults = correctExercise(learnerSolution, user, XmlExercise.finder.byId(exerciseId));

    if(request().acceptedTypes().get(0).toString().equals("application/json"))
	  // print this JSON-tree!!! to know what is inside
      return ok(Json.toJson(elementResults));
    else
      return ok(xmlcorrect.render(learnerSolution, elementResults, UserControl.getUser()));
  }
  
  private List<ElementResult> correctExercise(String solutionText, User user, XmlExercise exercise) {
    return XmlCorrector.correct(solutionText, exercise, user);
  }

  private String extractLearnerSolutionFromRequest(Request request) {
    return request.body().asFormUrlEncoded().get(LEARNER_SOLUTION_VALUE)[0];
  }
  
  private void saveSolutionForUser(String userName, String solution, int exercise) {
    try {
      if(!Files.exists(Util.getSolDirForUser(userName)))
        Files.createDirectory(Util.getSolDirForUser(userName));

      Path solDir = Util.getSolDirForUserAndType("xml", userName);
      if(!Files.exists(solDir))
        Files.createDirectory(solDir);

      Path saveTo = Util.getXmlSolFileForExercise(userName, exercise);
      Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException error) {
      Logger.error("Fehler beim Speichern einer Xml-Loesungsdatei!", error);
    }
  }
  
}
