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
import play.api.mvc.Call;
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
  
  public AbstractAdminController(FormFactory theFactory, Finder<Integer, E> theFinder, R theExerciseReader) {
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
    if(finder.byId(exerciseId).delete())
      return ok("Aufgabe konnte gelöscht werden!");
    else
      return badRequest("Konnte nicht gelöscht werden!");
  }
  
  public Result exportExercises() {
    return ok(views.html.export.render(getUser(), Json.prettyPrint(Json.toJson(finder.all()))));
  }
  
  public Result getJSONSchemaFile() {
    return ok(exerciseReader.getJsonSchemaFile().toFile());
  }
  
  public Result importExercises() {
    AbstractReadingResult abstractResult = exerciseReader.readFromStandardFile();
    
    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError) abstractResult));
    
    @SuppressWarnings("unchecked")
    ReadingResult<E> result = (ReadingResult<E>) abstractResult;
    
    result.getRead().forEach(exerciseReader::saveRead);
    return ok(views.html.admin.preview.render(getUser(), renderCreated(result.getRead()), getIndex()));
  }
  
  public abstract Result index();
  
  public E initFromForm(DynamicForm form) {
    int id = findMinimalNotUsedId(finder);
    
    String title = form.get(StringConsts.TITLE_NAME);
    String author = form.get(StringConsts.AUTHOR_NAME);
    String text = form.get(StringConsts.TEXT_NAME);
    
    return initRemainingExFromForm(id, title, author, text, form);
    
  }
  
  public Result newExercise() {
    E exercise = initFromForm(factory.form().bindFromRequest());
    exerciseReader.saveRead(exercise);
    return ok(views.html.admin.preview.render(getUser(), renderCreated(Arrays.asList(exercise)), getIndex()));
  }
  
  public abstract Result newExerciseForm();
  
  public abstract Html renderCreated(List<E> created);
  
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
    return ok(views.html.admin.preview.render(getUser(), renderCreated(result.getRead()), getIndex()));
  }
  
  protected abstract Call getIndex();
  
  protected Path getSampleDir() {
    return Paths.get(BASE_DATA_PATH, SAMPLE_SUB_DIRECTORY, exerciseReader.getExerciseType());
  }
  
  protected abstract E initRemainingExFromForm(int id, String title, String author, String text, DynamicForm form);
  
}
