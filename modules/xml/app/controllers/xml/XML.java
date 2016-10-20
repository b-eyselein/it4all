package controllers.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.Secured;
import model.Util;
import model.XMLError;
import model.XmlCorrector;
import model.XmlErrorType;
import model.XmlExercise;
import model.XmlExercise.XmlExType;
import model.exercise.EvaluationResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import play.twirl.api.Html;
import views.html.xml;
import views.html.xmlcorrect;
import views.html.xmloverview;

@Security.Authenticated(Secured.class)
public class XML extends ExerciseController {

  private static final String EXERCISE_TYPE = "xml";
  private static final String LEARNER_SOLUTION_VALUE = "editorContent";
  private static final String STANDARD_XML = "";

  @SuppressWarnings("unused")
  private XmlStartUpChecker checker;

  @Inject
  public XML(Util theUtil, FormFactory theFactory, XmlStartUpChecker theChecker) {
    super(theUtil, theFactory);
    checker = theChecker;
  }

  public Result commit(int exerciseId) {
    User user = UserManagement.getCurrentUser();
    XmlExercise exercise = XmlExercise.finder.byId(exerciseId);

    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);

    if(exercise.exerciseType == XmlExType.XML_DTD) {
      // FIXME: do not save fixed start with solution?!?
      learnerSolution = generateFixedStart(exercise,
          util.getSampleFileForExercise(EXERCISE_TYPE, exercise.referenceFileName).toString()) + "\n" + learnerSolution;
    }

    Path solutionPath = saveSolutionForUser(user, learnerSolution, exercise);

    List<EvaluationResult> elementResults;
    if(exercise.exerciseType == XmlExType.XML_XSD && learnerSolution.contains("!DOCTYPE")) {
      // TODO: evtl. woanders?
      elementResults = Arrays
          .asList(new XMLError("Fehler beim Erstellen des Dokuments:", "benutze kein DTD!", XmlErrorType.FATALERROR));
    } else {
      elementResults = correctExercise(solutionPath, user, exercise);
    }

    if(wantsJsonResponse())
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
    String defaultOrOldSolution = loadOldSolution(exerciseId, exercise, user);

    String referenceCode = loadReferenceCode(exercise);

    String fixedStart = "";
    if(exercise.exerciseType == XmlExType.XML_DTD) {
      fixedStart = generateFixedStart(exercise, exercise.referenceFileName);
      if(defaultOrOldSolution.startsWith("<?xml")) {
        // Remove fixed start from old solution
        defaultOrOldSolution = defaultOrOldSolution.substring(defaultOrOldSolution.indexOf('\n') + 1);
        defaultOrOldSolution = defaultOrOldSolution.substring(defaultOrOldSolution.indexOf('\n') + 1);
      }
    }

    return ok(xml.render(UserManagement.getCurrentUser(), exercise, referenceCode, defaultOrOldSolution, fixedStart));
  }

  public Result index() {
    return ok(xmloverview.render(XmlExercise.finder.all(), UserManagement.getCurrentUser()));
  }

  private List<EvaluationResult> correctExercise(Path solutionPath, User user, XmlExercise exercise) {
    Path referenceFile = loadReferenceFile(solutionPath, user, exercise);

    List<EvaluationResult> result = XmlCorrector.correct(solutionPath.toFile(), referenceFile.toFile(), exercise);

    if(result.isEmpty())
      return Arrays.asList(new XMLError("Alles richtig!", "", XmlErrorType.NONE));

    return result;
  }

  private Path createCustomReferenceFileforUser(Path solutionPath, User user, XmlExercise exercise) {
    Path result = util.getSolFileForExercise(user, EXERCISE_TYPE, "reference_for_" + exercise.id + "." + "xml");
    String content = "";
    try {
      content = generateFixedStart(exercise, solutionPath.toString()) + "\n" + String.join("\n",
          Files.readAllLines(util.getSampleFileForExercise(EXERCISE_TYPE, exercise.referenceFileName))) + "\n";

      Files.write(result, Arrays.asList(content), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      Logger.error("Fehler beim Lesen oder Schreiben einer XML-Referenzdatei:", e);
    }
    return result;
  }

  private String generateFixedStart(XmlExercise exercise, String dtdPathString) {
    // + exercise.rootElementName +
    String rootElementName = exercise.referenceFileName.split("\\.")[0];
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<!DOCTYPE " + rootElementName + " SYSTEM \""
        + dtdPathString + "\">";
  }

  private String loadOldSolution(int exerciseId, XmlExercise exercise, User user) {
    Path oldSolutionPath = util.getSolFileForExercise(user, EXERCISE_TYPE, exerciseId,
        exercise.exerciseType.getFileEnding());

    if(Files.exists(oldSolutionPath, LinkOption.NOFOLLOW_LINKS)) {
      try {
        return String.join("\n", Files.readAllLines(oldSolutionPath));
      } catch (IOException e) {
        Logger.error("There has been an error reading a old solution:", e);
      }
    }

    return STANDARD_XML;
  }

  private String loadReferenceCode(XmlExercise exercise) {
    Path referenceFilePath = util.getSampleFileForExercise(EXERCISE_TYPE, exercise.referenceFileName);
    if(Files.exists(referenceFilePath, LinkOption.NOFOLLOW_LINKS)) {
      try {
        return String.join("\n", Files.readAllLines(referenceFilePath));
      } catch (IOException e) {
        Logger.error("There has been an error loading the reference code in " + referenceFilePath.toString(), e);
      }
    }
    return "";
  }

  private Path loadReferenceFile(Path solutionPath, User user, XmlExercise exercise) {
    switch(exercise.exerciseType) {
    case XML_DTD:
    case XML_XSD:
      return util.getSampleFileForExercise(EXERCISE_TYPE, exercise.referenceFileName);
    case DTD_XML:
      return createCustomReferenceFileforUser(solutionPath, user, exercise);
    case XSD_XML:
    default:
      return null;
    }
  }

  private Path saveSolutionForUser(User user, String solution, XmlExercise exercise) {
    try {
      Path solDir = util.getSolDirForUserAndType(user, EXERCISE_TYPE);
      if(!Files.exists(solDir))
        Files.createDirectories(solDir);

      Path saveTo = util.getSolFileForExercise(user, EXERCISE_TYPE, exercise.id, exercise.exerciseType.getFileEnding());
      Files.write(saveTo, Arrays.asList(solution), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      return saveTo;
    } catch (IOException error) {
      Logger.error("Fehler beim Speichern einer Xml-Loesungsdatei!", error);
    }
    return null;
  }

}
