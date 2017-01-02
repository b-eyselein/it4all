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
import controllers.core.UserManagement;
import model.IntExerciseIdentifier;
import model.Util;
import model.XMLError;
import model.XmlCorrector;
import model.XmlErrorType;
import model.XmlExercise;
import model.XmlExercise.XmlExType;
import model.blanks.BlanksExercise;
import model.exercise.Success;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.result.CompleteResult;
import model.result.EvaluationResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import views.html.xml;
import views.html.correction;
import views.html.xmloverview;
import views.html.blanks;
import play.mvc.Http.Request;

public class XML extends ExerciseController<IntExerciseIdentifier> {

  private static final String EXERCISE_TYPE = "xml";
  private static final String LEARNER_SOLUTION_VALUE = "editorContent";
  private static final String STANDARD_XML = "";

  private static final String SAVE_ERROR_MSG = "An error has occured while saving an xml file to ";

  @Inject
  public XML(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public Result commit(IntExerciseIdentifier identifier) {
    User user = UserManagement.getCurrentUser();
    CompleteResult result = correct(request(), user, identifier);

    if(wantsJsonResponse()) {
      log(user, new ExerciseCorrectionEvent(request(), identifier, result));
      return ok(Json.toJson(result));
    } else {
      log(user, new ExerciseCompletionEvent(request(), identifier, result));
      return ok(correction.render("XML", result, user));
    }
  }

  public Result correctBlanks(int id) {
    DynamicForm form = factory.form().bindFromRequest();
    int inputCount = Integer.parseInt(form.get("count"));

    List<String> inputs = new ArrayList<>(inputCount);
    for(int count = 0; count < inputCount; count++)
      inputs.add(form.get("inp" + count));

    BlanksExercise exercise = new BlanksExercise();
    List<Success> results = exercise.correct(inputs);

    return ok(Json.toJson(results));
  }

  public Result exercise(IntExerciseIdentifier identifier) {
    XmlExercise exercise = XmlExercise.finder.byId(identifier.id);

    if(exercise == null)
      return redirect(controllers.xml.routes.XML.index(Collections.emptyList()));

    User user = UserManagement.getCurrentUser();

    String defaultOrOldSolution = loadOldSolution(exercise, user);
    String referenceCode = loadReferenceCode(exercise);

    if(exercise.exerciseType == XmlExType.XML_DTD && defaultOrOldSolution.startsWith("<?xml")) {
      // FIXME: Remove fixed start from old solution
      defaultOrOldSolution = defaultOrOldSolution.substring(defaultOrOldSolution.indexOf('\n') + 1);
      defaultOrOldSolution = defaultOrOldSolution.substring(defaultOrOldSolution.indexOf('\n') + 1);
    }

    log(user, new ExerciseStartEvent(request(), identifier));

    return ok(xml.render(UserManagement.getCurrentUser(), exercise, identifier, referenceCode, defaultOrOldSolution));
  }

  public Result index(List<String> filter) {
    List<XmlExType> filters = filter.isEmpty() ? Arrays.asList(XmlExType.values())
        : filter.stream().map(XmlExType::valueOf).collect(Collectors.toList());
    List<XmlExercise> exercises = XmlExercise.finder.all().stream().filter(ex -> filters.contains(ex.exerciseType))
        .collect(Collectors.toList());
    return ok(xmloverview.render(UserManagement.getCurrentUser(), exercises, filters));
  }

  public Result testBlanks() {
    return ok(blanks.render(UserManagement.getCurrentUser(), new BlanksExercise()));
  }

  private Path checkAndCreateSolDir(User user, int id) {
    Path dir = Paths.get(util.getSolDirForUserAndType(user, EXERCISE_TYPE).toString(), Integer.toString(id));
    if(!dir.toFile().exists())
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
      return Arrays.asList(new XMLError("", XmlErrorType.NONE, -1));
    return result;
  }

  private String loadOldSolution(XmlExercise exercise, User user) {
    // FIXME: behebe Hack!
    Path oldSolutionPath = util.getSolFileForExercise(user, EXERCISE_TYPE,
        exercise.id + "/" + exercise.referenceFileName + "." + exercise.exerciseType.getFileEnding());

    if(oldSolutionPath.toFile().exists()) {
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
    if(referenceFilePath.toFile().exists()) {
      try {
        return String.join("\n", Files.readAllLines(referenceFilePath));
      } catch (IOException e) {
        Logger.error("There has been an error loading the reference code in " + referenceFilePath.toString(), e);
      }
    }
    return "";
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
    Path target = Paths.get(dir.toString(), exercise.referenceFileName + exercise.getGrammarFileEnding());
    Path source = Paths.get(util.getSampleFileForExercise(EXERCISE_TYPE, exercise.referenceFileName).toString()
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

  @Override
  protected CompleteResult correct(Request request, User user, IntExerciseIdentifier identifier) {
    // FIXME: implement!
    XmlExercise exercise = XmlExercise.finder.byId(identifier.id);

    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);

    if(exercise.exerciseType == XmlExType.XML_DTD) {
      // FIXME: do not save fixed start with solution?!?
      learnerSolution = exercise.fixedStart + "\n" + learnerSolution;
    }

    Path dir = checkAndCreateSolDir(user, exercise.id);

    Path grammar;
    Path xml;
    if(exercise.exerciseType == XmlExType.DTD_XML || exercise.exerciseType == XmlExType.XSD_XML) {
      grammar = saveGrammar(dir, learnerSolution, exercise);
      xml = saveXML(dir, exercise);
    } else {
      grammar = saveGrammar(dir, exercise);
      xml = saveXML(dir, learnerSolution, exercise);
    }

    List<EvaluationResult> elementResults = correctExercise(xml, grammar, exercise);

    return new CompleteResult(learnerSolution, elementResults);
  }

}
