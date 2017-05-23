package controllers.uml;

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
import model.StringConsts;
import model.UmlExTextParser;
import model.UmlExercise;
import model.UmlExerciseReader;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.twirl.api.Html;

public class UmlAdmin extends AbstractAdminController<UmlExercise, UmlExerciseReader> {

  @Inject
  public UmlAdmin(FormFactory theFactory) {
    super(theFactory, UmlExercise.finder, "uml", new UmlExerciseReader());
  }

  public Result checkSolution() {
    DynamicForm form = factory.form().bindFromRequest();

    JsonNode solNode = null;
    try {
      solNode = Json.parse(form.get("solution"));
    } catch (Exception e) {
      return ok("error");
    }

    Optional<ProcessingReport> report = JsonWrapper.validateJson(solNode, Uml.SOLUTION_SCHEMA_NODE);

    if(!report.isPresent())
      return ok("error");
    else if(report.get().isSuccess())
      return ok("ok");
    else
      return ok(report.get().toString());
  }

  @Override
  public UmlExercise getNew(int id) {
    return new UmlExercise(id);
  }

  @Override
  public Result index() {
    return ok(views.html.umlAdmin.index.render(getUser()));
  }

  @Override
  public Result newExerciseForm() {
    return ok(views.html.umlAdmin.newExerciseStep1Form.render(getUser()));
  }

  public Result newExerciseStep2() {
    DynamicForm form = factory.form().bindFromRequest();

    String text = form.get(StringConsts.TEXT_NAME);

    UmlExTextParser parser = new UmlExTextParser(text, Collections.emptyMap(), Collections.emptyList());

    // exercise does not get saved, so take maximum id
    UmlExercise exercise = new UmlExercise(Integer.MAX_VALUE);
    exercise.title = form.get(StringConsts.TITLE_NAME);
    exercise.text = text;
    exercise.classSelText = parser.parseTextForClassSel();
    exercise.diagDrawText = parser.parseTextForDiagDrawing();

    return ok(views.html.umlAdmin.newExerciseStep2Form.render(getUser(), exercise, parser.getCapitalizedWords()));
  }

  public Result newExerciseStep3() {
    DynamicForm form = factory.form().bindFromRequest();

    String text = form.get(StringConsts.TEXT_NAME);

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
    exercise.title = form.get(StringConsts.TITLE_NAME);
    exercise.text = text;
    exercise.classSelText = parser.parseTextForClassSel();
    exercise.diagDrawText = parser.parseTextForDiagDrawing();

    return ok(views.html.umlAdmin.newExerciseStep3Form.render(getUser(), exercise));
  }

  @Override
  public Html renderCreated(List<UmlExercise> exercises) {
    return views.html.umlCreation.render(exercises);
  }

  @Override
  protected void initRemainingExFromForm(DynamicForm form, UmlExercise exercise) {
    exercise.classSelText = form.get("classSelText");
    exercise.diagDrawText = form.get("diagDrawText");
    exercise.solution = form.get("solution");

    // return ok(views.html.umlAdmin.newExerciseCreated.render(getUser(),
    // newExercise));
  }

}
