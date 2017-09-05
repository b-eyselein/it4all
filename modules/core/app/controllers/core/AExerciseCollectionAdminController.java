package controllers.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    extends AExerciseAdminController<E> {
  
  protected final Finder<Integer, C> collectionFinder;
  
  protected final ExerciseCollectionReader<E, C> exerciseCollectionReader;
  
  public AExerciseCollectionAdminController(FormFactory theFactory, RoutesObject theRoutes,
      Finder<Integer, E> theExerciseFinder, Finder<Integer, C> theCollectionFinder,
      ExerciseCollectionReader<E, C> theExerciseReader) {
    super(theFactory, theRoutes, theExerciseFinder, theExerciseReader.getDelegateReader());
    collectionFinder = theCollectionFinder;
    exerciseCollectionReader = theExerciseReader;
  }
  
  protected static Path saveUploadedFile(Path savingDir, Path pathToUploadedFile, Path saveTo) {
    if(!savingDir.toFile().exists() && !ExerciseReader.createDirectory(savingDir))
      // error occured...
      return null;
    
    try {
      return Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING);
    } catch (final IOException e) {
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
    return ok(views.html.admin.export.render(getUser(), Json.prettyPrint(Json.toJson(collectionFinder.all()))));
  }
  
  @Override
  public Result getJSONSchemaFile() {
    return ok(Json.prettyPrint(exerciseCollectionReader.jsonSchema()));
  }
  
  public Result importExerciseCollections() {
    final AbstractReadingResult abstractResult = exerciseCollectionReader.readFromStandardFile();
    
    if(!(abstractResult instanceof ReadingResult<?>))
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError) abstractResult));
    
    @SuppressWarnings("unchecked")
    final ReadingResult<C> result = (ReadingResult<C>) abstractResult;
    
    result.read().forEach(exerciseCollectionReader::saveExercise);
    return ok(views.html.admin.preview.render(getUser(), renderCollectionCreated(result.read())));
  }
  
  public Result newExerciseCollection(int collectionId) {
    // final C exercise = exerciseCollectionReader.initFromForm(collectionId,
    // factory.form().bindFromRequest());
    // exerciseCollectionReader.saveExercise(exercise);
    // return ok(views.html.admin.preview.render(getUser(),
    // renderCollectionCreated(Arrays.asList(exercise))));
    return ok("TODO!");
  }
  
  public Result newExerciseCollectionForm() {
    final int id = ExerciseReader.findMinimalNotUsedId(collectionFinder);
    final C collection = collectionFinder.byId(id);
    return ok(renderExCollCreationForm(getUser(), collection));
  }
  
  @Override
  public Result uploadFile() {
    final MultipartFormData<File> body = request().body().asMultipartFormData();
    final FilePart<File> uploadedFile = body.getFile(StringConsts.BODY_FILE_NAME);
    
    if(uploadedFile == null)
      return badRequest("Fehler!");
    
    final Path pathToUploadedFile = uploadedFile.getFile().toPath();
    final Path savingDir = Paths.get(BASE_DATA_PATH, StringConsts.ADMIN_FOLDER,
        exerciseCollectionReader.exerciseType());
    
    final Path jsonFile = Paths.get(savingDir.toString(), uploadedFile.getFilename());
    final Path jsonTargetPath = saveUploadedFile(savingDir, pathToUploadedFile, jsonFile);
    
    final AbstractReadingResult abstractResult = exerciseCollectionReader.readFromJsonFile(jsonTargetPath);
    
    if(!(abstractResult instanceof ReadingResult<?>))
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError) abstractResult));
    
    @SuppressWarnings("unchecked")
    final ReadingResult<C> result = (ReadingResult<C>) abstractResult;
    
    result.read().forEach(exerciseCollectionReader::saveExercise);
    return ok(views.html.admin.preview.render(getUser(), renderCollectionCreated(result.read())));
  }
  
  @Override
  protected Path getSampleDir() {
    return Paths.get(BASE_DATA_PATH, SAMPLE_SUB_DIRECTORY, exerciseCollectionReader.exerciseType());
  }
  
  protected abstract Html renderCollectionCreated(List<C> created);
  
  protected abstract Html renderExCollCreationForm(User user, C collection);
  
  protected abstract Html renderExerciseCollections(User user, List<C> allCollections);
  
}
