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
import model.CommonUtils$;
import model.StringConsts;
import model.exercise.Exercise;
import model.exercise.ExerciseState;
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ExerciseReader;
import model.exercisereading.ReadingError;
import model.exercisereading.ReadingResult;
import model.tools.ToolObject;
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

  protected final ToolObject routes;

  protected final Finder<Integer, E> finder;
  protected final ExerciseReader<E> exerciseReader;

  public AExerciseAdminController(FormFactory theFactory, ToolObject theRoutes, Finder<Integer, E> theFinder,
      ExerciseReader<E> theExerciseReader) {
    super(theFactory);
    routes = theRoutes;
    finder = theFinder;
    exerciseReader = theExerciseReader;
  }

  protected static Path saveUploadedFile(Path savingDir, Path pathToUploadedFile, Path saveTo) {
    if(!savingDir.toFile().exists() && !CommonUtils$.MODULE$.createDirectory(savingDir))
      // error occured...
      return null;

    try {
      return Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING);
    } catch (final IOException e) {
      Logger.error("Error while saving uploaded sql file!", e);
      return null;
    }
  }

  public abstract Result adminIndex();

  public Result changeExState(int id) {
    final E exercise = finder.byId(id);
    final ExerciseState newState = ExerciseState.valueOf(factory.form().bindFromRequest().get("state"));

    exercise.state = newState;
    exercise.save();

    return ok(Json.parse("{\"id\": \"" + id + "\", \"newState\": \"" + exercise.state + "\"}"));
  }

  public Result deleteExercise(int id) {
    final E toDelete = finder.byId(id);
    if(toDelete == null) {
      return badRequest(Json.parse(
          "{\"message\": \"Die Aufgabe mit ID " + id + " existiert nicht und kann daher nicht gelöscht werden!\""));
    }

    if(toDelete.delete()) {
      return ok(Json.parse("{\"id\": \"" + id + "\"}"));
    } else {
      return badRequest(
          Json.parse("{\"message\": \"Es gab einen internen Fehler beim Löschen der Aufgabe mit der ID " + id + "\""));
    }
  }

  public Result editExercise(int id) {
    final E exercise = exerciseReader.initFromForm(id, factory.form().bindFromRequest());
    exerciseReader.save(exercise);
    return ok(views.html.admin.preview.render(getUser(), renderExercises(Arrays.asList(exercise), false)));
  }

  public Result editExerciseForm(int id) {
    final E exercise = finder.byId(id);

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
    return ok(Json.prettyPrint(exerciseReader.jsonSchema()));
  }

  public Result importExercises() {
    final AbstractReadingResult abstractResult = exerciseReader.readFromStandardFile();

    if(!(abstractResult instanceof ReadingResult<?>))
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError) abstractResult));

    @SuppressWarnings("unchecked")
    final ReadingResult<E> result = (ReadingResult<E>) abstractResult;

    result.read().forEach(exerciseReader::save);
    return ok(views.html.admin.preview.render(getUser(), renderExercises(result.read(), false)));
  }

  public Result newExerciseForm() {
    final int id = ExerciseReader.findMinimalNotUsedId(finder);

    final E exercise = exerciseReader.getOrInstantiateExercise(id);
    exerciseReader.save(exercise);

    return ok(renderExEditForm(getUser(), exercise, true));
  }

  public Result uploadFile() {
    final MultipartFormData<File> body = request().body().asMultipartFormData();
    final FilePart<File> uploadedFile = body.getFile(StringConsts.BODY_FILE_NAME);

    if(uploadedFile == null)
      return badRequest("Fehler!");

    final Path pathToUploadedFile = uploadedFile.getFile().toPath();
    final Path savingDir = Paths.get(BASE_DATA_PATH, StringConsts.ADMIN_FOLDER, exerciseReader.exerciseType());

    final Path jsonFile = Paths.get(savingDir.toString(), uploadedFile.getFilename());
    final Path jsonTargetPath = saveUploadedFile(savingDir, pathToUploadedFile, jsonFile);

    final AbstractReadingResult abstractResult = exerciseReader.readFromJsonFile(jsonTargetPath);

    if(!(abstractResult instanceof ReadingResult<?>))
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError) abstractResult));

    @SuppressWarnings("unchecked")
    final ReadingResult<E> result = (ReadingResult<E>) abstractResult;

    result.read().forEach(exerciseReader::save);
    return ok(views.html.admin.preview.render(getUser(), renderExercises(result.read(), false)));
  }

  protected Path getSampleDir() {
    return Paths.get(BASE_DATA_PATH, SAMPLE_SUB_DIRECTORY, exerciseReader.exerciseType());
  }

  protected abstract Html renderExEditForm(User user, E exercise, boolean isCreation);

  protected Html renderExercises(List<E> exercises, boolean changesAllowed) {
    return views.html.admin.exercisesTable.render(exercises, routes, changesAllowed);
  }

}
