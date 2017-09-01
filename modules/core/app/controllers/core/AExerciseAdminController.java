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

  private static class DeletionResult {
    private int id;
    private String message;

    @SuppressWarnings("unused")
    public int getExerciseId() {
      return id;
    }

    @SuppressWarnings("unused")
    public String getMessage() {
      return message;
    }

    public void setExerciseId(int id) {
      this.id = id;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }

  protected final RoutesObject routes;

  protected final Finder<Integer, E> finder;
  protected final ExerciseReader<E> exerciseReader;

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

  public AExerciseAdminController(FormFactory theFactory, RoutesObject theRoutes, Finder<Integer, E> theFinder,
      ExerciseReader<E> theExerciseReader) {
    super(theFactory);
    routes = theRoutes;
    finder = theFinder;
    exerciseReader = theExerciseReader;
  }

  public Result deleteExercise(int id) {
    DeletionResult result = new DeletionResult();
    result.setExerciseId(id);
    E toDelete = finder.byId(id);

    if(toDelete == null) {
      result
          .setMessage(String.format("Die Aufgabe mit ID %s existiert nicht und kann daher nicht gelöscht werden!", id));
      return badRequest(Json.toJson(result));
    }

    if(toDelete.delete()) {
      result.setMessage(String.format("Die Aufgabe mit der ID %s konnte erfolgreich gelöscht werden.", id));
      return ok(Json.toJson(result));
    } else {
      result.setMessage(String.format("Es gab einen internen Fehler beim Löschen der Aufgabe mit der ID %s!", id));
      return badRequest(Json.toJson(result));
    }
  }

  public Result editExercise(int id) {
    E exercise = exerciseReader.initFromForm(id, factory.form().bindFromRequest());
    exerciseReader.saveExercise(exercise);
    return ok(views.html.admin.preview.render(getUser(), renderExercises(Arrays.asList(exercise), false)));
  }

  public Result editExerciseForm(int id) {
    E exercise = finder.byId(id);

    if(exercise == null)
      return badRequest("");

    return ok(renderExEditForm(getUser(), exercise, false));
  }

  public Result exercises() {
    return ok(views.html.admin.exerciseList.render(getUser(), renderExercises(finder.all(), true)));
  }

  public Result exportExercises() {
    return ok(views.html.admin.export.render(getUser(), Json.prettyPrint(Json.toJson(finder.all()))));
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

    result.getRead().forEach(exerciseReader::saveExercise);
    return ok(views.html.admin.preview.render(getUser(), renderExercises(result.getRead(), false)));
  }

  public abstract Result adminIndex();

  public Result newExerciseForm() {
    int id = ExerciseReader.findMinimalNotUsedId(finder);

    E exercise = exerciseReader.getOrInstantiateExercise(id);
    exerciseReader.saveExercise(exercise);

    return ok(renderExEditForm(getUser(), exercise, true));
  }

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

    result.getRead().forEach(exerciseReader::saveExercise);
    return ok(views.html.admin.preview.render(getUser(), renderExercises(result.getRead(), false)));
  }

  protected Path getSampleDir() {
    return Paths.get(BASE_DATA_PATH, SAMPLE_SUB_DIRECTORY, exerciseReader.getExerciseType());
  }

  protected abstract Html renderExEditForm(User user, E exercise, boolean isCreation);

  protected Html renderExercises(List<E> exercises, boolean changesAllowed) {
    return views.html.admin.exercisesTable.render(exercises, routes, changesAllowed);
  }

}
