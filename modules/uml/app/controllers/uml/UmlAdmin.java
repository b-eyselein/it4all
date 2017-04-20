package controllers.uml;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;

import controllers.core.AbstractAdminController;
import model.UmlExTextParser;
import model.UmlExercise;
import model.UmlExerciseReader;
import model.Util;
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ReadingError;
import model.exercisereading.ReadingResult;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class UmlAdmin extends AbstractAdminController<UmlExercise, UmlExerciseReader> {

  // FIXME: entfernen!
  // @formatter:off
  private static final ImmutableMap<String, String> MAPPINGS = new ImmutableMap.Builder<String, String>()
      .put("Krankenschwestern", "Krankenschwester")
      .put("Doktoren", "Doktor")
      .put("Stationen", "Station")
      .put("Patienten", "Patient")
      .put("Medikamente", "Medikament")
      .build();
  // @formatter:on
  
  @Inject
  public UmlAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, "uml", new UmlExerciseReader());
  }

  public Result index() {
    return ok(views.html.umlAdmin.umlAdmin.render(getUser()));
  }

  public Result newExercise() {
    DynamicForm form = factory.form().bindFromRequest();

    String title = form.get("title");
    String text = form.get("text");

    // @formatter:off
    List<String> toIngore =
        Arrays.stream(form.get("ignore").split(" "))
        .filter(str -> str != null && !str.isEmpty())
        .collect(Collectors.toList());
    // @formatter:on

    // FIXME: Search exercise with same title, override!
    UmlExercise newExercise = UmlExercise.finder.where().eq("title", title).findUnique();

    if(newExercise == null)
      newExercise = new UmlExercise(findMinimalNotUsedId(UmlExercise.finder));

    UmlExTextParser parser = new UmlExTextParser(text, MAPPINGS, toIngore);

    newExercise.title = title;
    newExercise.text = text;

    newExercise.classSelText = parser.parseTextForClassSel();
    newExercise.diagDrawText = text;
    newExercise.diagDrawHelpText = text;

    newExercise.save();

    return ok(views.html.umlAdmin.umlSingleCreation.render(getUser(), newExercise));
  }

  public Result newExerciseForm() {
    return ok(views.html.umlAdmin.newExerciseForm.render(getUser()));
  }

  @Override
  public Result readStandardExercises() {
    AbstractReadingResult<UmlExercise> abstractResult = exerciseReader.readStandardExercises();

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError<UmlExercise>) abstractResult));

    ReadingResult<UmlExercise> result = (ReadingResult<UmlExercise>) abstractResult;

    saveExercises(result.getRead());
    return ok(views.html.preview.render(getUser(), views.html.umlcreation.render(result.getRead())));
  }

  @Override
  public Result uploadFile() {
    MultipartFormData<File> body = request().body().asMultipartFormData();
    FilePart<File> uploadedFile = body.getFile(BODY_FILE_NAME);

    if(uploadedFile == null)
      return badRequest("Fehler!");

    Path pathToUploadedFile = uploadedFile.getFile().toPath();
    Path savingDir = Paths.get(util.getRootSolDir().toString(), ADMIN_FOLDER, exerciseType);

    Path jsonFile = Paths.get(savingDir.toString(), uploadedFile.getFilename());
    saveUploadedFile(savingDir, pathToUploadedFile, jsonFile);

    AbstractReadingResult<UmlExercise> abstractResult = exerciseReader.readStandardExercises();

    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError<UmlExercise>) abstractResult));

    ReadingResult<UmlExercise> result = (ReadingResult<UmlExercise>) abstractResult;

    saveExercises(result.getRead());
    return ok(views.html.preview.render(getUser(), views.html.umlcreation.render(result.getRead())));
  }

  @Override
  public Result uploadForm() {
    return ok("TODO!");
  }

  @Override
  protected void saveExercises(List<UmlExercise> exercises) {
    exercises.forEach(UmlExercise::save);
  }

}
