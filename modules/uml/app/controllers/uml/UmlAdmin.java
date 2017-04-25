package controllers.uml;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import controllers.core.AbstractAdminController;
import model.JsonWrapper;
import model.UmlExTextParser;
import model.UmlExercise;
import model.UmlExerciseReader;
import model.Util;
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ReadingError;
import model.exercisereading.ReadingResult;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class UmlAdmin extends AbstractAdminController<UmlExercise, UmlExerciseReader> {

  private static final String TITLE_NAME = "title";
  private static final String TEXT_NAME = "text";

  @Inject
  public UmlAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, "uml", new UmlExerciseReader());
  }

  public Result checkSolution() {
    DynamicForm form = factory.form().bindFromRequest();
    
    JsonNode solNode = null;
    try {
      solNode = Json.parse(form.get("solution"));
    } catch (Exception e) {
      return ok("error");
    }
    
    Optional<ProcessingReport> report = JsonWrapper.validateJson(solNode, UML.SOLUTION_SCHEMA_NODE);

    if(!report.isPresent())
      return ok("error");
    else if(report.get().isSuccess())
      return ok("ok");
    else
      return ok(report.get().toString());
  }

  public Result index() {
    return ok(views.html.umlAdmin.umlAdmin.render(getUser()));
  }

  public Result newExercise() {
    DynamicForm form = factory.form().bindFromRequest();

    String title = form.get(TITLE_NAME);

    // FIXME: Search exercise with same title, override!
    UmlExercise newExercise = UmlExercise.finder.where().eq(TITLE_NAME, title).findUnique();

    if(newExercise == null)
      newExercise = new UmlExercise(findMinimalNotUsedId(UmlExercise.finder));

    newExercise.title = title;
    newExercise.text = form.get(TEXT_NAME);
    newExercise.classSelText = form.get("classSelText");
    newExercise.diagDrawText = form.get("diagDrawText");
    newExercise.solution = form.get("solution");

    // newExercise.save();

    return ok(views.html.umlAdmin.newExerciseCreated.render(getUser(), newExercise));
  }

  public Result newExerciseStep1() {
    return ok(views.html.umlAdmin.newExerciseStep1Form.render(getUser()));
  }

  public Result newExerciseStep2() {
    DynamicForm form = factory.form().bindFromRequest();

    String text = form.get(TEXT_NAME);

    UmlExTextParser parser = new UmlExTextParser(text, Collections.emptyMap(), Collections.emptyList());

    // exercise does not get saved, so take maximum id
    UmlExercise exercise = new UmlExercise(Integer.MAX_VALUE);
    exercise.title = form.get(TITLE_NAME);
    exercise.text = text;
    exercise.classSelText = parser.parseTextForClassSel();
    exercise.diagDrawText = parser.parseTextForDiagDrawing();

    return ok(views.html.umlAdmin.newExerciseStep2Form.render(getUser(), exercise, parser.getCapitalizedWords()));
  }

  public Result newExerciseStep3() {
    DynamicForm form = factory.form().bindFromRequest();

    String text = form.get(TEXT_NAME);

    List<String> toIgnore = new LinkedList<>();
    Map<String, String> mappings = new HashMap<>();

    for(String capWord: UmlExTextParser.getCapitalizedWords(text)) {
      switch(form.get(capWord)) {
      case "ignore":
        toIgnore.add(capWord);
        break;
      case "baseform":
        mappings.put(capWord, form.get(capWord + "_baseform"));
        break;
      case "none":
      default:
        // Do nothing...
        break;
      }
    }

    UmlExTextParser parser = new UmlExTextParser(text, mappings, toIgnore);

    // exercise does not get saved, so take maximum id
    UmlExercise exercise = new UmlExercise(Integer.MAX_VALUE);
    exercise.title = form.get(TITLE_NAME);
    exercise.text = text;
    exercise.classSelText = parser.parseTextForClassSel();
    exercise.diagDrawText = parser.parseTextForDiagDrawing();

    return ok(views.html.umlAdmin.newExerciseStep3Form.render(getUser(), exercise));
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
