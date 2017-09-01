package controllers.uml;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import controllers.core.AExerciseAdminController;
import model.StringConsts;
import model.UmlExTextParser;
import model.UmlExercise;
import model.UmlExerciseReader;
import model.exercisereading.ExerciseReader;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.twirl.api.Html;

public class UmlAdmin extends AExerciseAdminController<UmlExercise> {

  @Inject
  public UmlAdmin(FormFactory theFactory) {
    super(theFactory, UmlRoutesObject$.MODULE$, UmlExercise.finder, UmlExerciseReader.getInstance());
  }

  @Override
  public Result adminIndex() {
    return ok(views.html.umlAdmin.index.render(getUser()));
  }

  public Result checkSolution() {
    DynamicForm form = factory.form().bindFromRequest();

    JsonNode solNode = Json.parse(form.get(StringConsts.SOLUTION_NAME));
    try {
      ProcessingReport report = ExerciseReader.validateJson(solNode, UmlController.SOLUTION_SCHEMA_NODE);

      if(report.isSuccess())
        return ok("ok");

      return ok(report.toString());
    } catch (ProcessingException e) {
      return badRequest("Fehler!");
    }
  }

  public Result newExerciseStep2() {
    UmlExercise exercise = exerciseReader.initFromForm(0, factory.form().bindFromRequest());
    UmlExTextParser parser = new UmlExTextParser(exercise.getText(), exercise.getMappings(), exercise.getToIgnore());
    return ok(views.html.umlAdmin.newExerciseStep2Form.render(getUser(), exercise, parser.getCapitalizedWords()));
  }

  public Result newExerciseStep3() {
    UmlExercise exercise = exerciseReader.initFromForm(0, factory.form().bindFromRequest());
    return ok(views.html.umlAdmin.newExerciseStep3Form.render(getUser(), exercise));
  }

  @Override
  protected Html renderExEditForm(User user, UmlExercise exercise, boolean isCreation) {
    // TODO Auto-generated method stub
    return null;
  }

}
