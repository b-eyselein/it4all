package controllers.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.CorrectionException;
import model.StringConsts;
import model.XmlCorrector;
import model.XmlError;
import model.XmlExType;
import model.XmlExercise;
import model.XmlExerciseReader;
import model.exercisereading.ExerciseReader;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class XmlController extends ExerciseController<XmlExercise, XmlError> {

  public static final String STANDARD_XML_PLAYGROUND = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "\n<!DOCTYPE root [\n\n]>\n";

  private static final String SAVE_ERROR_MSG = "An error has occured while saving an xml file to ";

  @Inject
  public XmlController(FormFactory theFactory) {
    super(theFactory, "xml", XmlExercise.finder);
  }

  public String getReferenceCode(XmlExercise exercise) {
    Path referenceFilePath = Paths.get(getSampleDir().toString(),
        exercise.getReferenceFileName() + "." + exercise.getReferenceFileEnding());
    return referenceFilePath.toFile().exists() ? XmlExerciseReader.readFile(referenceFilePath) : "FEHLER!";
  }

  public Result index(List<String> filter) {
    // @formatter:off
    List<XmlExType> filters = filter.isEmpty() ?
        Arrays.asList(XmlExType.values()) :
        filter.stream().map(XmlExType::valueOf).collect(Collectors.toList());

    List<XmlExercise> exercises = XmlExercise.finder.all().stream()
        .filter(ex -> filters.contains(ex.getExerciseType()))
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

  private String readDefOrOldSolution(String username, XmlExercise exercise) {
    Path oldSolutionPath = getSolFileForExercise(username, exerciseType, exercise, exercise.getReferenceFileName(),
        exercise.getStudentFileEnding());
    return oldSolutionPath.toFile().exists() ? ExerciseReader.readFile(oldSolutionPath) : exercise.getFixedStart();
  }

  private Path saveGrammar(Path dir, String learnerSolution, XmlExercise exercise) {
    Path grammar = Paths.get(dir.toString(), exercise.getReferenceFileName() + exercise.getGrammarFileEnding());

    try {
      return Files.write(grammar, Arrays.asList(learnerSolution.split(StringConsts.NEWLINE)), StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException error) {
      Logger.error("Fehler beim Speichern einer Xml-Loesungsdatei!", error);
      return null;
    }
  }

  private Path saveGrammar(Path dir, XmlExercise exercise) {
    String filename = exercise.getReferenceFileName() + "." + exercise.getGrammarFileEnding();

    Path target = Paths.get(dir.toString(), filename);
    Path source = Paths.get(getSampleDir().toString(), filename);

    try {
      return Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException error) {
      Logger.error(SAVE_ERROR_MSG + target.toString(), error);
      return null;
    }
  }

  private Path saveXML(Path dir, String learnerSolution, XmlExercise exercise) {
    Path targetFile = Paths.get(dir.toString(), exercise.getReferenceFileName() + ".xml");

    try {
      return Files.write(targetFile, Arrays.asList(learnerSolution.split(StringConsts.NEWLINE)),
          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException error) {
      Logger.error(SAVE_ERROR_MSG + targetFile.toString(), error);
      return null;
    }
  }

  private Path saveXML(Path dir, XmlExercise exercise) {
    String filename = exercise.getReferenceFileName() + ".xml";

    Path target = Paths.get(dir.toString(), filename);
    Path source = Paths.get(getSampleDir().toString(), filename);

    try {
      return Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException error) {
      Logger.error(SAVE_ERROR_MSG + target.toString(), error);
      return null;
    }
  }

  @Override
  protected List<XmlError> correct(String learnerSolution, XmlExercise exercise, User user) {
    Path dir = checkAndCreateSolDir(user.name, exercise);

    Path grammar;
    Path xml;
    if(exercise.getExerciseType() == XmlExType.DTD_XML || exercise.getExerciseType() == XmlExType.XSD_XML) {
      grammar = saveGrammar(dir, learnerSolution, exercise);
      xml = saveXML(dir, exercise);
    } else {
      grammar = saveGrammar(dir, exercise);
      xml = saveXML(dir, learnerSolution, exercise);
    }

    try {
      return XmlCorrector.correct(xml, grammar, exercise);
    } catch (CorrectionException e) {
      return Arrays.asList(new XmlError(null, null));
    }
  }

  @Override
  protected Html renderExercise(User user, XmlExercise exercise) {
    String defOrOldSolution = readDefOrOldSolution(user.name, exercise);
    return views.html.xmlExercise.render(user, exercise, getReferenceCode(exercise), defOrOldSolution);
  }

  @Override
  protected Html renderExercises(User user, List<XmlExercise> exercises) {
    return views.html.xmlExercises.render(user, exercises);
  }

  @Override
  protected Html renderResult(List<XmlError> correctionResult) {
    return views.html.xmlResult.render(correctionResult);
  }

}
