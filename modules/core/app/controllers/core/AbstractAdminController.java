package controllers.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.avaje.ebean.Model.Finder;

import model.AdminSecured;
import model.exercise.Exercise;
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ExerciseReader;
import model.exercisereading.ReadingError;
import model.exercisereading.ReadingResult;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import play.twirl.api.Html;

@Authenticated(AdminSecured.class)
public abstract class AbstractAdminController<E extends Exercise, R extends ExerciseReader<E>>
    extends AbstractController {

  protected static final String BODY_FILE_NAME = "file";
  protected static final String ADMIN_FOLDER = "admin";

  protected final Path resourcesPath = Paths.get("conf", "resources", exerciseType);

  protected Finder<Integer, E> finder;

  protected R exerciseReader;

  public AbstractAdminController(FormFactory theFactory, Finder<Integer, E> theFinder, String theExerciseType,
      R theExerciseReader) {
    super(theFactory, theExerciseType);
    finder = theFinder;
    exerciseReader = theExerciseReader;
  }

  protected static void saveUploadedFile(Path savingDir, Path pathToUploadedFile, Path saveTo) {
    try {
      if(!savingDir.toFile().exists() && !savingDir.toFile().isDirectory())
        Files.createDirectories(savingDir);
      Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      Logger.error("Error while saving uploaded sql file!", e);
    }
  }

  public Result exportExercises() {
    return ok(views.html.export.render(getUser(), Json.prettyPrint(Json.toJson(finder.all()))));
  }

  public Result getJSONSchemaFile() {
    return ok(Paths.get(resourcesPath.toString(), "exerciseSchema.json").toFile());
  }

  public Result importExercises() {
    AbstractReadingResult<E> abstractResult = exerciseReader.readStandardExercises();

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError<E>) abstractResult));

    ReadingResult<E> result = (ReadingResult<E>) abstractResult;

    result.getRead().forEach(ex -> exerciseReader.saveExercise(ex));
    return ok(views.html.preview.render(getUser(), renderCreated(result.getRead())));
  }

  public abstract Result index();

  public abstract Result newExercise();

  public abstract Result newExerciseForm();

  public abstract Html renderCreated(List<E> created);

  public Result uploadFile() {
    MultipartFormData<File> body = request().body().asMultipartFormData();
    FilePart<File> uploadedFile = body.getFile(BODY_FILE_NAME);

    if(uploadedFile == null)
      return badRequest("Fehler!");

    Path pathToUploadedFile = uploadedFile.getFile().toPath();
    Path savingDir = Paths.get(BASE_DATA_PATH, ADMIN_FOLDER, exerciseType);

    Path jsonFile = Paths.get(savingDir.toString(), uploadedFile.getFilename());
    saveUploadedFile(savingDir, pathToUploadedFile, jsonFile);

    AbstractReadingResult<E> abstractResult = exerciseReader.readStandardExercises();

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError<E>) abstractResult));

    ReadingResult<E> result = (ReadingResult<E>) abstractResult;

    result.getRead().forEach(exerciseReader::saveExercise);
    return ok(views.html.preview.render(getUser(), renderCreated(result.getRead())));
  }

  public abstract Result uploadForm();

}
