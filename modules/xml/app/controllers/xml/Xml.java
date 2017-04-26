package controllers.xml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.Util;
import model.XmlCorrector;
import model.XmlError;
import model.XmlErrorType;
import model.XmlExType;
import model.XmlExercise;
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
  
  @Inject
  public Xml(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public Result commit(int id) {
    User user = getUser();
    XmlExercise exercise = XmlExercise.finder.byId(id);
    
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);
    
    List<XmlError> correctionResult = correct(learnerSolution, exercise);
    
    log(user, new ExerciseCompletionEvent(request(), id, correctionResult));
    
    return ok(
        views.html.correction.render("XML", views.html.xmlresult.render(correctionResult), learnerSolution, user));
  }
  
  public Result correctLive(int id) {
    User user = getUser();
    XmlExercise exercise = XmlExercise.finder.byId(id);
    
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);
    
    List<XmlError> result = correct(learnerSolution, exercise);
    
    log(user, new ExerciseCorrectionEvent(request(), id, result));
    
    return ok(views.html.xmlresult.render(result));
  }
  
  public Result exercise(int id) {
    User user = getUser();
    XmlExercise exercise = XmlExercise.finder.byId(id);
    
    String defOrOldSolution = readDefOrOldSolution(user, exercise);
    String refCode = exercise.getReferenceCode(util);
    
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
    
    return ok(views.html.xmloverview.render(getUser(), exercises, filters));
  }
  
  private List<XmlError> correct(String learnerSolution, XmlExercise exercise) {
    String grammar;
    String xml;
    if(exercise.exerciseType == XmlExType.DTD_XML || exercise.exerciseType == XmlExType.XSD_XML) {
      grammar = learnerSolution;
      xml = "";
    } else {
      grammar = "";
      xml = learnerSolution;
    }
    
    return correctExercise(xml, grammar, exercise);
  }
  
  private List<XmlError> correctExercise(String xml, String grammar, XmlExercise exercise) {
    List<XmlError> result = XmlCorrector.correct(xml, grammar, exercise.exerciseType);
    
    if(result.isEmpty())
      return Arrays.asList(new XmlError("", XmlErrorType.NONE, -1));
    
    return result;
  }
  
  private Path getOldSolPath(User user, XmlExercise exercise) {
    return util.getSolFileForExercise(user, EXERCISE_TYPE,
        exercise.id + "/" + exercise.referenceFileName + "." + exercise.getStudentFileEnding());
  }
  
  private String readDefOrOldSolution(User user, XmlExercise exercise) {
    String defOrOldSolution = exercise.fixedStart;
    try {
      Path oldSolutionPath = getOldSolPath(user, exercise);
      if(oldSolutionPath.toFile().exists())
        defOrOldSolution = Util.readFileFromPath(oldSolutionPath);
    } catch (IOException e) {
      Logger.error("There has been an error reading the file", e);
    }
    return defOrOldSolution;
  }
  
}
