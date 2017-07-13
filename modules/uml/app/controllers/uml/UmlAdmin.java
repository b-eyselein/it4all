package controllers.uml;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import controllers.core.AbstractAdminController;
import model.JsonWrapper;
import model.Mapping;
import model.StringConsts;
import model.UmlExTextParser;
import model.UmlExercise;
import model.UmlExerciseReader;
import play.api.mvc.Call;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.twirl.api.Html;

public class UmlAdmin extends AbstractAdminController<UmlExercise, UmlExerciseReader> {

  @Inject
  public UmlAdmin(FormFactory theFactory) {
    super(theFactory, UmlExercise.finder, UmlExerciseReader.getInstance());
  }

  public Result checkSolution() {
    DynamicForm form = factory.form().bindFromRequest();

    JsonNode solNode = Json.parse(form.get(StringConsts.SOLUTION_NAME));

    ProcessingReport report = JsonWrapper.validateJson(solNode, UmlController.SOLUTION_SCHEMA_NODE);

    if(report.isSuccess())
      return ok("ok");

    return ok(report.toString());
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

    // exercise does not get saved, so take maximum id
    int id = Integer.MAX_VALUE;
    String title = form.get(StringConsts.TITLE_NAME);
    String author = form.get(StringConsts.AUTHOR_NAME);
    String text = form.get(StringConsts.TEXT_NAME);

    UmlExTextParser parser = new UmlExTextParser(text, Collections.emptyList(), Collections.emptyList());

    String classSelText = parser.parseTextForClassSel();
    String diagDrawText = parser.parseTextForDiagDrawing();
    UmlExercise exercise = new UmlExercise(id, title, author, text, classSelText, diagDrawText, "");

    return ok(views.html.umlAdmin.newExerciseStep2Form.render(getUser(), exercise, parser.getCapitalizedWords()));
  }

  public Result newExerciseStep3() {
    DynamicForm form = factory.form().bindFromRequest();

    int id = Integer.MAX_VALUE;
    String title = form.get(StringConsts.TITLE_NAME);
    String author = form.get(StringConsts.AUTHOR_NAME);

    String text = form.get(StringConsts.TEXT_NAME);

    List<String> toIgnore = new LinkedList<>();
    List<Mapping> mappings = new LinkedList<>();

    for(String capWord: UmlExTextParser.getCapitalizedWords(text)) {
      // FIXME: one comma separated string!
      switch(form.get(capWord)) {
      case "ignore":
        toIgnore.add(capWord);
        break;
      case "baseform":
        mappings.add(new Mapping(capWord, form.get(capWord + "_baseform")));
        break;
      case "none":
      default:
        // Do nothing...
        break;
      }
    }

    UmlExTextParser parser = new UmlExTextParser(text, mappings, toIgnore);
    String classSelText = parser.parseTextForClassSel();
    String diagDrawText = parser.parseTextForDiagDrawing();

    UmlExercise exercise = new UmlExercise(id, title, author, text, classSelText, diagDrawText, "");

    return ok(views.html.umlAdmin.newExerciseStep3Form.render(getUser(), exercise));
  }

  @Override
  public Html renderCreated(List<UmlExercise> exercises) {
    return views.html.umlCreation.render(exercises);
  }

  @Override
  protected Call getIndex() {
    return controllers.uml.routes.UmlAdmin.index();
  }

  @Override
  protected UmlExercise initRemainingExFromForm(int id, String title, String author, String text, DynamicForm form) {
    String classSelText = form.get("classSelText");
    String diagDrawText = form.get("diagDrawText");
    String solution = form.get("solution");
    return new UmlExercise(id, title, author, text, classSelText, diagDrawText, solution);
  }

}
