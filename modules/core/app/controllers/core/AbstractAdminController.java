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
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import play.twirl.api.Html;

@Authenticated(AdminSecured.class)
public abstract class AbstractAdminController<E extends Exercise, R extends ExerciseReader<E>> extends BaseController {

  protected Finder<Integer, E> finder;

  protected R exerciseReader;

  protected String exerciseType;

  public AbstractAdminController(FormFactory theFactory, Finder<Integer, E> theFinder, String theExerciseType,
      R theExerciseReader) {
    super(theFactory);
    finder = theFinder;
    exerciseType = theExerciseType;
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

  public Result exportExercises() {
    return ok(views.html.export.render(getUser(), Json.prettyPrint(Json.toJson(finder.all()))));
  }

  public Result getJSONSchemaFile() {
    return ok(exerciseReader.getJsonSchemaFile().toFile());
  }

  public abstract E getNew(int id);

  public Result importExercises() {
    AbstractReadingResult<E> abstractResult = exerciseReader.readStandardExercises();

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError<E>) abstractResult));

    ReadingResult<E> result = (ReadingResult<E>) abstractResult;

    result.getRead().forEach(exerciseReader::saveExercise);
    return ok(views.html.preview.render(getUser(), renderCreated(result.getRead())));
  }

  public abstract Result index();

  public E initFromForm(DynamicForm form) {
    String title = form.get(StringConsts.TITLE_NAME);

    E exercise = findByTitle(title);
    if(exercise == null)
      exercise = getNew(findMinimalNotUsedId(finder));

    exercise.author = form.get(StringConsts.AUTHOR_NAME);
    exercise.text = form.get(StringConsts.TEXT_NAME);
    exercise.title = form.get(StringConsts.TITLE_NAME);

    initRemainingExFromForm(form, exercise);

    return exercise;
  }

  public Result newExercise() {
    E exercise = initFromForm(factory.form().bindFromRequest());
    exerciseReader.saveExercise(exercise);
    return ok(views.html.preview.render(getUser(), renderCreated(Arrays.asList(exercise))));
  }

  public abstract Result newExerciseForm();

  public abstract Html renderCreated(List<E> created);

  public Result uploadFile() {
    MultipartFormData<File> body = request().body().asMultipartFormData();
    FilePart<File> uploadedFile = body.getFile(StringConsts.BODY_FILE_NAME);

    if(uploadedFile == null)
      return badRequest("Fehler!");

    Path pathToUploadedFile = uploadedFile.getFile().toPath();
    Path savingDir = Paths.get(BASE_DATA_PATH, StringConsts.ADMIN_FOLDER, exerciseType);

    Path jsonFile = Paths.get(savingDir.toString(), uploadedFile.getFilename());
    Path jsonTargetPath = saveUploadedFile(savingDir, pathToUploadedFile, jsonFile);

    AbstractReadingResult<E> abstractResult = exerciseReader.readExercises(jsonTargetPath);

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError<E>) abstractResult));

    ReadingResult<E> result = (ReadingResult<E>) abstractResult;

    result.getRead().forEach(exerciseReader::saveExercise);
    return ok(views.html.preview.render(getUser(), renderCreated(result.getRead())));
  }

  protected E findByTitle(String title) {
    return finder.all().stream().filter(e -> title.equals(e.title)).findFirst().orElse(null);
  }

  protected Path getSampleDir() {
    return Paths.get(BASE_DATA_PATH, SAMPLE_SUB_DIRECTORY, exerciseType);
  }

  protected abstract void initRemainingExFromForm(DynamicForm form, E exercise);

}
