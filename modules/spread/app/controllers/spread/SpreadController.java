package controllers.spread;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.CorrectionException;
import model.SpreadExercise;
import model.SpreadSheetCorrectionResult;
import model.SpreadSheetCorrector;
import model.result.EvaluationResult;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;

public class SpreadController extends ExerciseController<SpreadExercise, EvaluationResult> {

  private static final String BODY_SOL_FILE_NAME = "solFile";

  @Inject
  public SpreadController(FormFactory theFactory) {
    super(theFactory, "spread", SpreadExercise.finder);
  }

  public Result download(int id, String typ) {
    SpreadExercise exercise = SpreadExercise.finder.byId(id);

    if(exercise == null)
      return badRequest("This exercise does not exist!");

    Path fileToDownload = Paths.get(BASE_DATA_PATH, SOLUTIONS_SUB_DIRECTORY, exerciseType,
        exercise.templateFilename + "_Korrektur." + typ);

    if(!fileToDownload.toFile().exists())
      return badRequest("TODO!");
    // views.html.error.render(user, "<p>Die Korrigierte Datei existiert
    // nicht!</p><p>Zur&uuml;ck zur <a href=\""
    // + controllers.spread.routes.SpreadController.index() +
    // "\">&Uuml;bersichtsseite</a></p>"));

    return ok(fileToDownload.toFile());
  }

  public Result downloadTemplate(int id, String fileType) {
    SpreadExercise exercise = SpreadExercise.finder.byId(id);

    if(exercise == null)
      return badRequest("This exercise does not exist!");

    Path filePath = Paths.get(getSampleDir().toString(), exercise.templateFilename + "." + fileType);

    if(!filePath.toFile().exists())
      return badRequest("This file does not exist!");

    return ok(filePath.toFile());
  }

  public Result exercises() {
    return ok(views.html.spreadExercises.render(getUser(), SpreadExercise.finder.all()));
  }

  public Result index() {
    return ok(views.html.spreadoverview.render(getUser(), SpreadExercise.finder.all()));
  }

  public Result upload(int id) {
    User user = getUser();
    SpreadExercise exercise = SpreadExercise.finder.byId(id);

    // Extract solution from request
    MultipartFormData<File> body = request().body().asMultipartFormData();
    FilePart<File> uploadedFile = body.getFile(BODY_SOL_FILE_NAME);
    if(uploadedFile == null)
      return internalServerError(
          views.html.spreadcorrectionerror.render(user, "Datei konnte nicht hochgeladen werden!"));
    Path pathToUploadedFile = uploadedFile.getFile().toPath();

    String fileExtension = SpreadSheetCorrector.getExtension(uploadedFile.getFilename());

    // Save solution
    Path targetFilePath = getSolFileForExercise(user.name, exerciseType, exercise, exercise.templateFilename,
        fileExtension);
    boolean fileSuccessfullySaved = saveSolutionForUser(pathToUploadedFile, targetFilePath);
    if(!fileSuccessfullySaved)
      return internalServerError(
          views.html.spreadcorrectionerror.render(user, "Die Datei konnte nicht gespeichert werden!"));

    // Get paths to sample document
    Path sampleDocumentPath = Paths.get(getSampleDir().toString(), exercise.sampleFilename + "." + fileExtension);
    Logger.debug("Pfad der Musterdatei: " + sampleDocumentPath);
    if(!sampleDocumentPath.toFile().exists())
      return internalServerError(
          views.html.spreadcorrectionerror.render(user, "Die Musterdatei konnte nicht gefunden werden!"));

    SpreadSheetCorrectionResult result = SpreadSheetCorrector.correct(sampleDocumentPath, targetFilePath, false, false);

    if(result.isSuccess())
      return ok(views.html.excelcorrect.render(user, result, exercise.getId(), fileExtension));
    else
      return internalServerError(views.html.spreadcorrectionerror.render(user, result.getNotices().get(0)));

  }

  private boolean saveSolutionForUser(Path uploadedSolution, Path targetFilePath) {
    try {
      Path solDirForExercise = targetFilePath.getParent();
      if(!solDirForExercise.toFile().exists() && !solDirForExercise.toFile().isDirectory())
        Files.createDirectories(solDirForExercise);

      Files.move(uploadedSolution, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
      return true;
    } catch (Exception e) {
      Logger.error("Fehler beim Speichern der Lösung!", e);
      return false;
    }
  }

  @Override
  protected List<EvaluationResult> correct(String learnerSolution, SpreadExercise exercise, User user)
      throws CorrectionException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Html renderExercise(User user, SpreadExercise exercise) {
    return views.html.spreadExercise.render(user, exercise);
  }

  @Override
  protected Html renderResult(List<EvaluationResult> correctionResult) {
    // TODO Auto-generated method stub
    return null;
  }

}
