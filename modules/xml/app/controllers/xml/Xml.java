package controllers.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.StringConsts;
import model.Util;
import model.XmlCorrector;
import model.XmlError;
import model.XmlErrorType;
import model.XmlExType;
import model.XmlExercise;
import model.blanks.BlanksExercise;
import model.exercise.Success;
import model.exercisereading.ExerciseReader;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

public class Xml extends ExerciseController {
  
  private static final String EXERCISE_TYPE = "xml";
  
  public static final String STANDARD_XML_PLAYGROUND = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "\n<!DOCTYPE root [\n\n]>\n";
  
  private static final String SAVE_ERROR_MSG = "An error has occured while saving an xml file to ";
  
  @Inject
  public Xml(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public Result correct(int id) {
    User user = getUser();
    XmlExercise exercise = XmlExercise.finder.byId(id);
    
    DynamicForm form = factory.form().bindFromRequest();
    form.data().forEach((k, v) -> System.out.println(k + " --> " + v));
    
    String learnerSolution = form.get(StringConsts.FORM_VALUE);
    
    Logger.debug(learnerSolution);
    
    List<XmlError> correctionResult = correct(learnerSolution, exercise, user);
    
    log(user, new ExerciseCompletionEvent(request(), id, correctionResult));
    
    return ok(
        views.html.correction.render("XML", views.html.xmlResult.render(correctionResult), learnerSolution, user));
  }
  
  public Result correctLive(int id) {
    User user = getUser();
    XmlExercise exercise = XmlExercise.finder.byId(id);
    
    String learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE);
    
    Logger.debug(learnerSolution);
    
    List<XmlError> correctionResult = correct(learnerSolution, exercise, user);
    
    log(user, new ExerciseCorrectionEvent(request(), id, correctionResult));
    
    return ok(views.html.xmlResult.render(correctionResult));
  }
  
  public Result exercise(int id) {
    User user = getUser();
    XmlExercise exercise = XmlExercise.finder.byId(id);
    
    String defOrOldSolution = readDefOrOldSolution(user, exercise);
    String refCode = exercise.getReferenceCode();
    
    log(user, new ExerciseStartEvent(request(), id));
    
    return ok(views.html.xmlExercise.render(user, exercise, refCode, defOrOldSolution));
  }
  
  public Result exercises() {
    return ok(views.html.xmlExercises.render(getUser(), XmlExercise.finder.all()));
  }
  
  public Result index(List<String> filter) {
    // @formatter:off
    List<XmlExType> filters = filter.isEmpty() ?
        Arrays.asList(XmlExType.values()) :
        filter.stream().map(XmlExType::valueOf).collect(Collectors.toList());

    List<XmlExercise> exercises = XmlExercise.finder.all().stream()
        .filter(ex -> filters.contains(ex.exerciseType))
        .collect(Collectors.toList());
    // @formatter:on
    
    return ok(views.html.xmlIndex.render(getUser(), exercises, filters));
  }
  
  public Result playground() {
    return ok(views.html.xmlPlayground.render(getUser()));
  }
  
  public Result playgroundCorrection() {
    List<XmlError> result = XmlCorrector.correct(factory.form().bindFromRequest().get(StringConsts.FORM_VALUE), "",
        XmlExType.XML_DTD);
    return ok(views.html.xmlResult.render(result));
  }
  
  // private List<XmlError> correct(String learnerSolution, XmlExercise
  // exercise) {
  // String grammar;
  // String xml;
  // if(exercise.exerciseType == XmlExType.DTD_XML || exercise.exerciseType ==
  // XmlExType.XSD_XML) {
  // grammar = learnerSolution;
  // xml = "";
  // } else {
  // grammar = "";
  // xml = learnerSolution;
  // }
  //
  // return XmlCorrector.correct(xml, grammar, exercise.exerciseType);
  // }
  
  private List<XmlError> correct(String learnerSolution, XmlExercise exercise, User user) {
    Path dir = checkAndCreateSolDir(user, EXERCISE_TYPE, exercise.id);
    
    Path grammar;
    Path xml;
    if(exercise.exerciseType == XmlExType.DTD_XML || exercise.exerciseType == XmlExType.XSD_XML) {
      grammar = saveGrammar(dir, learnerSolution, exercise);
      xml = saveXML(dir, exercise);
    } else {
      grammar = saveGrammar(dir, exercise);
      xml = saveXML(dir, learnerSolution, exercise);
    }

    return XmlCorrector.correct(xml, grammar, exercise);
  }
  
  private Path getOldSolPath(User user, XmlExercise exercise) {
    return util.getSolFileForExercise(user, EXERCISE_TYPE,
        exercise.id + "/" + exercise.referenceFileName + "." + exercise.getStudentFileEnding());
  }
  
  private String readDefOrOldSolution(User user, XmlExercise exercise) {
    Path oldSolutionPath = getOldSolPath(user, exercise);
    if(oldSolutionPath.toFile().exists())
      return ExerciseReader.readFile(oldSolutionPath);
    
    return exercise.fixedStart;
  }
  
  private Path saveGrammar(Path dir, String learnerSolution, XmlExercise exercise) {
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
  
  private Path saveGrammar(Path dir, XmlExercise exercise) {
    Path target = Paths.get(dir.toString(), exercise.referenceFileName + "." + exercise.getGrammarFileEnding());
    Path source = Paths.get(util.getSampleFileForExercise(EXERCISE_TYPE, exercise.referenceFileName).toString() + "."
        + exercise.getGrammarFileEnding());
    try {
      Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
      return target;
    } catch (IOException error) {
      Logger.error(SAVE_ERROR_MSG + target.toString(), error);
      return null;
    }
  }
  
  private Path saveXML(Path dir, String learnerSolution, XmlExercise exercise) {
    Path targetFile = Paths.get(dir.toString(), exercise.referenceFileName + ".xml");
    try {
      Files.write(targetFile, Arrays.asList(learnerSolution.split("\n")), StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
      return targetFile;
    } catch (IOException error) {
      Logger.error(SAVE_ERROR_MSG + targetFile.toString(), error);
      return null;
    }
  }
  
  private Path saveXML(Path dir, XmlExercise exercise) {
    Path target = Paths.get(dir.toString(), exercise.referenceFileName + ".xml");
    Path source = Paths
        .get(util.getSampleFileForExercise(EXERCISE_TYPE, exercise.referenceFileName).toString() + ".xml");
    try {
      Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
      return target;
    } catch (IOException error) {
      Logger.error(SAVE_ERROR_MSG + target.toString(), error);
      return null;
    }
  }
  
}
