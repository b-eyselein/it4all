package controllers.spread;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import controllers.core.UserManagement;
import model.SpreadExercise;
import model.SpreadSheetCorrectionResult;
import model.SpreadSheetCorrector;
import model.Util;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;

public class Spread extends ExerciseController {

  private static final String EXERCISE_TYPE = "spread";
  private static final String BODY_SOL_FILE_NAME = "solFile";

  @Inject
  public Spread(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public Result download(int id, String typ) {
    User user = UserManagement.getCurrentUser();
    SpreadExercise exercise = SpreadExercise.finder.byId(id);

    if(exercise == null)
      return badRequest("This exercise does not exist!");

    Path fileToDownload = Paths.get(util.getSolDirForUserAndType(user, EXERCISE_TYPE).toString(),
        exercise.templateFilename + "_Korrektur." + typ);

    if(!fileToDownload.toFile().exists())
      return badRequest(views.html.error.render(user,
          new Html("<p>Die Korrigierte Datei existiert nicht!</p><p>Zur&uuml;ck zur <a href=\"" + routes.Spread.index()
              + "\">&Uuml;bersichtsseite</a></p>")));

    return ok(fileToDownload.toFile());
  }

  public Result downloadTemplate(int id, String fileType) {
    SpreadExercise exercise = SpreadExercise.finder.byId(id);

    if(exercise == null)
      return badRequest("This exercise does not exist!");

    Path filePath = Paths.get(util.getSampleDirForExerciseType(EXERCISE_TYPE).toString(),
        exercise.templateFilename + "." + fileType);

    if(!filePath.toFile().exists())
      return badRequest("This file does not exist!");

    return ok(filePath.toFile());
  }

  public Result index() {
    return ok(views.html.spreadoverview.render(UserManagement.getCurrentUser(), SpreadExercise.finder.all()));
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

  public Result upload(int id) {
    User user = UserManagement.getCurrentUser();
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
    Path targetFilePath = util.getSolFileForExercise(user, EXERCISE_TYPE,
        exercise.templateFilename + "." + fileExtension);
    boolean fileSuccessfullySaved = saveSolutionForUser(pathToUploadedFile, targetFilePath);
    if(!fileSuccessfullySaved)
      return internalServerError(
          views.html.spreadcorrectionerror.render(user, "Die Datei konnte nicht gespeichert werden!"));

    // Get paths to sample document
    Path sampleDocumentPath = Paths.get(util.getSampleDirForExerciseType(EXERCISE_TYPE).toString(),
        exercise.sampleFilename + "." + fileExtension);
    if(!sampleDocumentPath.toFile().exists())
      return internalServerError(
          views.html.spreadcorrectionerror.render(user, "Die Musterdatei konnte nicht gefunden werden!"));

    SpreadSheetCorrectionResult result = SpreadSheetCorrector.correct(sampleDocumentPath, targetFilePath, false, false);

    if(result.isSuccess())
      return ok(views.html.excelcorrect.render(user, result, exercise.id, fileExtension));
    else
      return internalServerError(views.html.spreadcorrectionerror.render(user, result.getNotices().get(0)));

  }

}
