package controllers.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.CommonUtils$;
import model.CorrectionException;
import model.StringConsts;
import model.XmlCorrector;
import model.XmlError;
import model.XmlExType;
import model.XmlExercise;
import model.exercise.ExerciseOptions;
import model.result.CompleteResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class XmlController extends ExerciseController<XmlExercise, XmlError> {

  private static final ExerciseOptions EX_OPTIONS = new ExerciseOptions("Xml", "xml", 10, 20, false);

  public static final String STANDARD_XML_PLAYGROUND = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "\n<!DOCTYPE root [\n\n]>\n";

  private static final String SAVE_ERROR_MSG = "An error has occured while saving an xml file to ";

  @Inject
  public XmlController(FormFactory theFactory) {
    super(theFactory, "xml", XmlExercise.finder, XmlRoutesObject$.MODULE$);
  }

  public static String getReferenceCode(XmlExercise exercise) {
    final Path referenceFilePath = Paths.get(getSampleDir("xml").toString(),
        exercise.rootNode + "." + exercise.getReferenceFileEnding());
    return referenceFilePath.toFile().exists() ? CommonUtils$.MODULE$.readFile(referenceFilePath) : "FEHLER!";
  }

  public Result playground() {
    return ok(views.html.xmlPlayground.render(getUser()));
  }

  public Result playgroundCorrection() {
    final List<XmlError> result = XmlCorrector.correct(factory.form().bindFromRequest().get(StringConsts.FORM_VALUE),
        "", XmlExType.XML_DTD);
    return ok(views.html.xmlResult.render(result));
  }

  private String readDefOrOldSolution(String username, XmlExercise exercise) {
    final Path oldSolutionPath = getSolFileForExercise(username, exerciseType, exercise, exercise.rootNode,
        exercise.getStudentFileEnding());
    return oldSolutionPath.toFile().exists() ? CommonUtils$.MODULE$.readFile(oldSolutionPath)
        : exercise.getFixedStart();
  }

  private Path saveGrammar(Path dir, String learnerSolution, XmlExercise exercise) {
    final Path grammar = Paths.get(dir.toString(), exercise.rootNode + exercise.getGrammarFileEnding());

    try {
      return Files.write(grammar, Arrays.asList(learnerSolution.split(StringConsts.NEWLINE)), StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (final IOException error) {
      Logger.error("Fehler beim Speichern einer Xml-Loesungsdatei!", error);
      return null;
    }
  }

  private Path saveGrammar(Path dir, XmlExercise exercise) {
    final String filename = exercise.rootNode + "." + exercise.getGrammarFileEnding();

    final Path target = Paths.get(dir.toString(), filename);
    final Path source = Paths.get(getSampleDir().toString(), filename);

    try {
      return Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (final IOException error) {
      Logger.error(SAVE_ERROR_MSG + target.toString(), error);
      return null;
    }
  }

  private Path saveXML(Path dir, String learnerSolution, XmlExercise exercise) {
    final Path targetFile = Paths.get(dir.toString(), exercise.rootNode + ".xml");

    try {
      return Files.write(targetFile, Arrays.asList(learnerSolution.split(StringConsts.NEWLINE)),
          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (final IOException error) {
      Logger.error(SAVE_ERROR_MSG + targetFile.toString(), error);
      return null;
    }
  }

  private Path saveXML(Path dir, XmlExercise exercise) {
    final String filename = exercise.rootNode + ".xml";

    final Path target = Paths.get(dir.toString(), filename);
    final Path source = Paths.get(getSampleDir().toString(), filename);

    try {
      return Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (final IOException error) {
      Logger.error(SAVE_ERROR_MSG + target.toString(), error);
      return null;
    }
  }

  @Override
  protected CompleteResult<XmlError> correct(DynamicForm form, XmlExercise exercise, User user) {
    final String learnerSolution = form.get(StringConsts.FORM_VALUE);
    final Path dir = checkAndCreateSolDir(user.name, exercise);

    Path grammar;
    Path xml;
    if(exercise.exerciseType == XmlExType.DTD_XML || exercise.exerciseType == XmlExType.XSD_XML) {
      grammar = saveGrammar(dir, learnerSolution, exercise);
      xml = saveXML(dir, exercise);
    } else {
      grammar = saveGrammar(dir, exercise);
      xml = saveXML(dir, learnerSolution, exercise);
    }

    List<XmlError> res;
    try {
      res = XmlCorrector.correct(xml, grammar, exercise);
    } catch (final CorrectionException e) {
      res = Arrays.asList(new XmlError(null, null));
    }
    return new CompleteResult<>(learnerSolution, res);
  }

  @Override
  protected Html renderExercise(User user, XmlExercise exercise) {
    final String defOrOldSolution = readDefOrOldSolution(user.name, exercise);
    return views.html.exercise2Rows.render(user, XmlRoutesObject$.MODULE$, EX_OPTIONS, exercise,
        views.html.xmlExRest.render(exercise), defOrOldSolution);
  }

  @Override
  protected Html renderExesListRest() {
    return views.html.xmlExesListRest.render();
  }

  @Override
  protected Html renderResult(CompleteResult<XmlError> correctionResult) {
    return views.html.xmlResult.render(correctionResult.getResults());
  }

}
