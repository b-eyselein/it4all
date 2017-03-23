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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.Util;
import model.XMLError;
import model.XmlCorrector;
import model.XmlErrorType;
import model.XmlExercise;
import model.XmlExercise.XmlExType;
import model.blanks.BlanksExercise;
import model.exercise.Success;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseStartEvent;
import model.result.CompleteResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;

public class XML extends ExerciseController {

  private static final String EXERCISE_TYPE = "xml";
  private static final String LEARNER_SOLUTION_VALUE = "editorContent";

  private static final String SAVE_ERROR_MSG = "An error has occured while saving an xml file to ";

  @Inject
  public XML(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public Result commit(int id) {
    User user = UserManagement.getCurrentUser();
    XmlExercise exercise = XmlExercise.finder.byId(id);

    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);

    List<XMLError> correctionResult = correct(learnerSolution, exercise, user);
    CompleteResult result = new CompleteResult(learnerSolution, new LinkedList<>(correctionResult));

    log(user, new ExerciseCompletionEvent(request(), id, result));

    return ok(views.html.correction.render("XML", result, user));
  }

  public Result correctBlanks(int id) {
    DynamicForm form = factory.form().bindFromRequest();
    int inputCount = Integer.parseInt(form.get("count"));

    List<String> inputs = new ArrayList<>(inputCount);
    for(int count = 0; count < inputCount; count++)
      inputs.add(form.get("inp" + count));

    BlanksExercise exercise = new BlanksExercise(id);
    List<Success> results = exercise.correct(inputs);

    return ok(Json.toJson(results));
  }

  public Result correctLive(int id) {
    User user = UserManagement.getCurrentUser();
    XmlExercise exercise = XmlExercise.finder.byId(id);

    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);

    List<XMLError> result = correct(learnerSolution, exercise, user);

    // log(user, new ExerciseCorrectionEvent(request(), id, result));

    return ok(views.html.xmlliveresult.render(result));
  }

  public Result exercise(int id) {
    User user = UserManagement.getCurrentUser();
    XmlExercise exercise = XmlExercise.finder.byId(id);

    if(exercise == null)
      return redirect(controllers.xml.routes.XML.index(Collections.emptyList()));

    String defOrOldSolution = readDefOrOldSolution(user, exercise);
    String refCode = exercise.getReferenceCode(util);

    log(user, new ExerciseStartEvent(request(), id));

    return ok(views.html.xml.render(user, exercise, refCode, defOrOldSolution));
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

    return ok(views.html.xmloverview.render(UserManagement.getCurrentUser(), exercises, filters));
  }

  public Result testBlanks() {
    return ok(views.html.blanks.render(UserManagement.getCurrentUser(), new BlanksExercise(1)));
  }

  private List<XMLError> correct(String learnerSolution, XmlExercise exercise, User user) {
    // FIXME: implement!
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

    return correctExercise(xml, grammar, exercise);
  }

  private List<XMLError> correctExercise(Path xml, Path grammar, XmlExercise exercise) {
    List<XMLError> result = XmlCorrector.correct(xml, grammar, exercise);

    if(result.isEmpty())
      return Arrays.asList(new XMLError("", XmlErrorType.NONE, -1));

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
    Path xml = Paths.get(dir.toString(), exercise.referenceFileName + ".xml");
    try {
      Files.write(xml, Arrays.asList(learnerSolution.split("\n")), StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
      return xml;
    } catch (IOException error) {
      Logger.error(SAVE_ERROR_MSG + xml.toString(), error);
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
