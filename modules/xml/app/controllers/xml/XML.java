package controllers.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
      learnerSolution = exercise.fixedStart + "\n" + learnerSolution;
    }
    
    // FIXME: check that directory for solution and reference file exists
    Path dir = checkAndCreateSolDir(user, exercise.id);
    
    Path grammar = null;
    Path xml = null;
    switch(exercise.exerciseType) {
    // FIXME: implement
    case DTD_XML:
    case XSD_XML:
      grammar = saveGrammar(dir, learnerSolution, exercise, user);
      xml = saveXML(dir, exercise);
      break;
    case XML_DTD:
    case XML_XSD:
      grammar = saveGrammar(dir, exercise, user);
      xml = saveXML(dir, learnerSolution, exercise, user);
      break;
    default:
      break;
    }
    
    if(grammar == null || xml == null)
      return badRequest();
    
    List<EvaluationResult> elementResults = correctExercise(xml, grammar, exercise);
    
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
    
    String defaultOrOldSolution = loadOldSolution(exercise, user);
    String referenceCode = loadReferenceCode(exercise);
    
    if(exercise.exerciseType == XmlExType.XML_DTD && defaultOrOldSolution.startsWith("<?xml")) {
      // // FIXME: Remove fixed start from old solution
      defaultOrOldSolution = defaultOrOldSolution.substring(defaultOrOldSolution.indexOf('\n') + 1);
      defaultOrOldSolution = defaultOrOldSolution.substring(defaultOrOldSolution.indexOf('\n') + 1);
    }
    
    return ok(xml.render(UserManagement.getCurrentUser(), exercise, referenceCode, defaultOrOldSolution,
        exercise.fixedStart));
  }
  
  public Result index() {
    return ok(xmloverview.render(XmlExercise.finder.all(), UserManagement.getCurrentUser()));
  }
  
  private Path checkAndCreateSolDir(User user, int id) {
    Path dir = Paths.get(util.getSolDirForUserAndType(user, EXERCISE_TYPE).toString(), Integer.toString(id));
    if(!Files.exists(dir))
      try {
        Files.createDirectories(dir);
      } catch (IOException e) {
        Logger.error("There was an error while creating the directory for the xml solution " + dir, e);
        return null;
      }
    return dir;
  }
  
  private List<EvaluationResult> correctExercise(Path xml, Path grammar, XmlExercise exercise) {
    List<EvaluationResult> result = XmlCorrector.correct(xml, grammar, exercise);
    if(result.isEmpty())
      return Arrays.asList(new XMLError("Alles richtig!", "", XmlErrorType.NONE));
    return result;
  }
  
  private String loadOldSolution(XmlExercise exercise, User user) {
    Path oldSolutionPath = util.getSolFileForExercise(user, EXERCISE_TYPE, exercise.referenceFileName,
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
    Path referenceFilePath = Paths
        .get(util.getSampleFileForExercise(EXERCISE_TYPE, exercise.referenceFileName).toString()
            + exercise.getReferenceFileEnding());
    if(Files.exists(referenceFilePath, LinkOption.NOFOLLOW_LINKS)) {
      try {
        return String.join("\n", Files.readAllLines(referenceFilePath));
      } catch (IOException e) {
        Logger.error("There has been an error loading the reference code in " + referenceFilePath.toString(), e);
      }
    }
    return "";
  }
  
  private Path saveGrammar(Path dir, String learnerSolution, XmlExercise exercise, User user) {
    Path grammar = Paths.get(dir.toString(), exercise.referenceFileName + exercise.getGrammarFileEnding());
    try {
      Files.write(grammar, Arrays.asList(learnerSolution.split("\n")), StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
      return grammar;
    } catch (IOException error) {
      Logger.error("Fehler beim Speichern einer Xml-Loesungsdatei!", error);
      return null;
    }
  }
  
  private Path saveGrammar(Path dir, XmlExercise exercise, User user) {
    // FIXME implement saveGrammar()
    return null;
  }
  
  private Path saveXML(Path dir, String learnerSolution, XmlExercise exercise, User user) {
    Path xml = Paths.get(dir.toString(), exercise.referenceFileName + ".xml");
    try {
      Files.write(xml, Arrays.asList(learnerSolution.split("\n")), StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
      return xml;
    } catch (IOException error) {
      Logger.error("An error has occured while savin an xml file to " + xml.toString(), error);
      return null;
    }
  }
  
  private Path saveXML(Path dir, XmlExercise exercise) {
    // FIXME implement saveXML()
    Path target = Paths.get(dir.toString(), exercise.referenceFileName + ".xml");
    Path source = Paths.get(util.getSampleFileForExercise(EXERCISE_TYPE, exercise.referenceFileName).toString()
        + exercise.getReferenceFileEnding());
    try {
      Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
      return target;
    } catch (IOException error) {
      Logger.error("An error has occured while savin an xml file to " + target.toString(), error);
      return null;
    }
  }
  
}
