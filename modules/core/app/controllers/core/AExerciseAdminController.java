package controllers.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import io.ebean.Finder;
import model.AdminSecured;
import model.StringConsts;
import model.exercise.Exercise;
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ExerciseReader;
import model.exercisereading.ReadingError;
import model.exercisereading.ReadingResult;
import model.user.User;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import play.twirl.api.Html;

@Authenticated(AdminSecured.class)
public abstract class AExerciseAdminController<E extends Exercise> extends BaseController {

  protected final Finder<Integer, E> finder;
  protected final ExerciseReader<E> exerciseReader;

  public AExerciseAdminController(FormFactory theFactory, Finder<Integer, E> theFinder,
      ExerciseReader<E> theExerciseReader) {
    super(theFactory);
    finder = theFinder;
    exerciseReader = theExerciseReader;
  }

  protected static Path saveUploadedFile(Path savingDir, Path pathToUploadedFile, Path saveTo) {
    if(!savingDir.toFile().exists() && !ExerciseReader.createDirectory(savingDir))
      // error occured...
      return null;

    try {
      return Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      Logger.error("Error while saving uploaded sql file!", e);
      return null;
    }
  }

  public Result deleteExercise(int exerciseId) {
    E toDelete = finder.byId(exerciseId);

    if(toDelete == null)
      return badRequest(
          String.format("Die Aufgabe mit ID %s existiert nicht und kann daher nicht gelöscht werden!", exerciseId));
    
    if(toDelete.delete())
      return ok(String.format("Die Aufgabe mit der ID %s konnte erfolgreich gelöscht werden.", exerciseId));
    else
      return badRequest(
          String.format("Es gab einen internen Fehler beim Löschen der Aufgabe mit der ID %s!", exerciseId));
  }

  public Result exercises() {
    return ok(renderExercises(getUser(), finder.all()));
  }

  public Result exportExercises() {
    return ok(views.html.export.render(getUser(), Json.prettyPrint(Json.toJson(finder.all()))));
  }

  public Result getJSONSchemaFile() {
    return ok(Json.prettyPrint(exerciseReader.getJsonSchema()));
  }

  public Result importExercises() {
    AbstractReadingResult abstractResult = exerciseReader.readFromStandardFile();

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError) abstractResult));

    @SuppressWarnings("unchecked")
    ReadingResult<E> result = (ReadingResult<E>) abstractResult;

    result.getRead().forEach(exerciseReader::saveRead);
    return ok(views.html.admin.preview.render(getUser(), renderCreated(result.getRead())));
  }

  public abstract Result index();

  public Result newExercise() {
    E exercise = exerciseReader.initFromForm(factory.form().bindFromRequest());
    exerciseReader.saveRead(exercise);
    return ok(views.html.admin.preview.render(getUser(), renderCreated(Arrays.asList(exercise))));
  }

  public abstract Result newExerciseForm();

  public Result uploadFile() {
    MultipartFormData<File> body = request().body().asMultipartFormData();
    FilePart<File> uploadedFile = body.getFile(StringConsts.BODY_FILE_NAME);

    if(uploadedFile == null)
      return badRequest("Fehler!");

    Path pathToUploadedFile = uploadedFile.getFile().toPath();
    Path savingDir = Paths.get(BASE_DATA_PATH, StringConsts.ADMIN_FOLDER, exerciseReader.getExerciseType());

    Path jsonFile = Paths.get(savingDir.toString(), uploadedFile.getFilename());
    Path jsonTargetPath = saveUploadedFile(savingDir, pathToUploadedFile, jsonFile);

    AbstractReadingResult abstractResult = exerciseReader.readAllFromFile(jsonTargetPath);

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError) abstractResult));

    @SuppressWarnings("unchecked")
    ReadingResult<E> result = (ReadingResult<E>) abstractResult;

    result.getRead().forEach(exerciseReader::saveRead);
    return ok(views.html.admin.preview.render(getUser(), renderCreated(result.getRead())));
  }

  protected Path getSampleDir() {
    return Paths.get(BASE_DATA_PATH, SAMPLE_SUB_DIRECTORY, exerciseReader.getExerciseType());
  }

  protected abstract Html renderCreated(List<E> created);

  protected abstract Html renderExercises(User user, List<E> allExercises);

}
