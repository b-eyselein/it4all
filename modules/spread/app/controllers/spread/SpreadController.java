package controllers.spread;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.CorrectionException;
import model.SpreadExercise;
import model.SpreadSheetCorrectionResult;
import model.SpreadSheetCorrector;
import model.result.CompleteResult;
import model.result.EvaluationResult;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;

public class SpreadController extends ExerciseController<SpreadExercise, EvaluationResult> {
  
  private static final String BODY_SOL_FILE_NAME = "solFile";
  private static final String CORRECTION_ADD_STRING = "_Korrektur";
  
  @Inject
  public SpreadController(FormFactory theFactory) {
    super(theFactory, "spread", SpreadExercise.finder, SpreadRoutesObject$.MODULE$);
  }
  
  public Result download(int id, String extension) {
    final User user = getUser();
    final SpreadExercise exercise = SpreadExercise.finder.byId(id);
    
    if(exercise == null)
      return badRequest("This exercise does not exist!");
    
    final Path fileToDownload = Paths.get(BASE_DATA_PATH, SOLUTIONS_SUB_DIRECTORY, user.name, exerciseType,
        Integer.toString(exercise.getId()), exercise.getTemplateFilename() + CORRECTION_ADD_STRING + "." + extension);
    if(!fileToDownload.toFile().exists())
      return redirect(routes.SpreadController.index(0));
    
    return ok(fileToDownload.toFile());
  }
  
  public Result downloadTemplate(int id, String fileType) {
    final SpreadExercise exercise = SpreadExercise.finder.byId(id);
    
    if(exercise == null)
      return badRequest("This exercise does not exist!");
    
    final Path filePath = Paths.get(getSampleDir().toString(), exercise.getTemplateFilename() + "." + fileType);
    
    if(!filePath.toFile().exists())
      return badRequest("This file does not exist!");
    
    return ok(filePath.toFile());
  }
  
  public Result upload(int id) {
    final User user = getUser();
    final SpreadExercise exercise = SpreadExercise.finder.byId(id);
    
    // Extract solution from request
    final MultipartFormData<File> body = request().body().asMultipartFormData();
    final FilePart<File> uploadedFile = body.getFile(BODY_SOL_FILE_NAME);
    if(uploadedFile == null)
      return internalServerError(
          views.html.spreadcorrectionerror.render(user, "Datei konnte nicht hochgeladen werden!"));
    final Path pathToUploadedFile = uploadedFile.getFile().toPath();
    final String fileExtension = com.google.common.io.Files.getFileExtension(uploadedFile.getFilename());
    
    // Save solution
    final Path targetFilePath = getSolFileForExercise(user.name, exerciseType, exercise, exercise.getTemplateFilename(),
        fileExtension);
    final boolean fileSuccessfullySaved = saveSolutionForUser(pathToUploadedFile, targetFilePath);
    if(!fileSuccessfullySaved)
      return internalServerError(
          views.html.spreadcorrectionerror.render(user, "Die Datei konnte nicht gespeichert werden!"));
    
    // Get paths to sample document
    final Path sampleDocumentPath = Paths.get(getSampleDir().toString(),
        exercise.getSampleFilename() + "." + fileExtension);
    if(!sampleDocumentPath.toFile().exists())
      return internalServerError(
          views.html.spreadcorrectionerror.render(user, "Die Musterdatei konnte nicht gefunden werden!"));
    try {
      final SpreadSheetCorrectionResult result = SpreadSheetCorrector.correct(sampleDocumentPath, targetFilePath, false,
          false);
      
      if(result.isSuccess())
        return ok(views.html.excelcorrect.render(user, result, exercise.getId(), fileExtension));
      else
        return internalServerError(views.html.spreadcorrectionerror.render(user, result.getNotices().get(0)));
    } catch (final CorrectionException e) {
      return internalServerError(views.html.spreadcorrectionerror.render(user, e.getMessage()));
    }
  }
  
  private boolean saveSolutionForUser(Path uploadedSolution, Path targetFilePath) {
    try {
      final Path solDirForExercise = targetFilePath.getParent();
      if(!solDirForExercise.toFile().exists() && !solDirForExercise.toFile().isDirectory())
        Files.createDirectories(solDirForExercise);
      
      Files.move(uploadedSolution, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
      return true;
    } catch (final Exception e) {
      Logger.error("Fehler beim Speichern der Lösung!", e);
      return false;
    }
  }
  
  @Override
  protected CompleteResult<EvaluationResult> correct(DynamicForm form, SpreadExercise exercise, User user)
      throws CorrectionException {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected Html renderExercise(User user, SpreadExercise exercise) {
    return views.html.spreadExercise.render(user, exercise);
  }
  
  @Override
  protected Html renderExesListRest() {
    // TODO Auto-generated method stub
    return new Html("");
  }
  
  @Override
  protected Html renderResult(CompleteResult<EvaluationResult> correctionResult) {
    // TODO Auto-generated method stub
    return new Html("");
  }
  
}
