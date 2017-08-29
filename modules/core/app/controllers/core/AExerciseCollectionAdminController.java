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
import model.exercise.ExerciseCollection;
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ExerciseCollectionReader;
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
public abstract class AExerciseCollectionAdminController<E extends Exercise, C extends ExerciseCollection<E>>
    extends BaseController {

  protected final Finder<Integer, C> collectionFinder;

  protected final ExerciseReader<C> exerciseReader;

  public AExerciseCollectionAdminController(FormFactory theFactory, Finder<Integer, C> theCollectionFinder,
      ExerciseCollectionReader<E, C> theExerciseReader) {
    super(theFactory);
    collectionFinder = theCollectionFinder;
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

  public Result deleteExerciseCollection(int exerciseId) {
    if(collectionFinder.byId(exerciseId).delete())
      return ok("Aufgabe konnte gelöscht werden!");
    else
      return badRequest("Konnte nicht gelöscht werden!");
  }

  public Result exerciseCollections() {
    return ok(renderExerciseCollections(getUser(), collectionFinder.all()));
  }

  public Result exportExerciseCollections() {
    return ok(views.html.export.render(getUser(), Json.prettyPrint(Json.toJson(collectionFinder.all()))));
  }

  public Result getJSONSchemaFile() {
    return ok(Json.prettyPrint(exerciseReader.getJsonSchema()));
  }

  public Result importExerciseCollections() {
    AbstractReadingResult abstractResult = exerciseReader.readFromStandardFile();

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError) abstractResult));

    @SuppressWarnings("unchecked")
    ReadingResult<C> result = (ReadingResult<C>) abstractResult;

    result.getRead().forEach(exerciseReader::saveRead);
    return ok(views.html.admin.preview.render(getUser(), renderCreated(result.getRead())));
  }

  public abstract Result index();

  public Result newExercise() {
    C exercise = exerciseReader.initFromForm(factory.form().bindFromRequest());
    exerciseReader.saveRead(exercise);
    return ok(views.html.admin.preview.render(getUser(), renderCreated(Arrays.asList(exercise))));
  }

  public abstract Result newExerciseCollectionForm();

  public abstract Result newExerciseForm();

  public abstract Html renderCreated(List<C> created);

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
    ReadingResult<C> result = (ReadingResult<C>) abstractResult;

    result.getRead().forEach(exerciseReader::saveRead);
    return ok(views.html.admin.preview.render(getUser(), renderCreated(result.getRead())));
  }

  protected Path getSampleDir() {
    return Paths.get(BASE_DATA_PATH, SAMPLE_SUB_DIRECTORY, exerciseReader.getExerciseType());
  }

  protected abstract Html renderExerciseCollections(User user, List<C> allCollections);

}
