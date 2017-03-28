package controllers.python;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.PythonCorrector;
import model.PythonExercise;
import model.Util;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.programming.ExecutionResult;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

public class Python extends ExerciseController {
  
  private static final PythonCorrector CORRECTOR = new PythonCorrector();
  
  @Inject
  public Python(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public Result correct(int id) {
    User user = getUser();
    PythonExercise exercise = new PythonExercise(id);
    // FIXME: Time out der Ausführung
    
    // Read commited solution and custom test data from request
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");
    
    List<ExecutionResult> result = correct(learnerSolution, exercise);
    
    log(user, new ExerciseCompletionEvent(request(), id, result));
    return ok(views.html.correction.render("Python", views.html.progresult.render(result), learnerSolution, user));
  }
  
  public Result correctLive(int id) {
    User user = getUser();
    PythonExercise exercise = new PythonExercise(id);
    // FIXME: Time out der Ausführung
    
    // Read commited solution and custom test data from request
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get("editorContent");
    
    List<ExecutionResult> result = correct(learnerSolution, exercise);
    
    log(user, new ExerciseCorrectionEvent(request(), id, result));
    return ok(views.html.progresult.render(result));
  }
  
  public Result exercise(int id) {
    return ok(views.html.programming.render(getUser(), PythonExercise.finder.byId(id)));
  }
  
  public Result index() {
    return ok(views.html.pythonoverview.render(getUser(), PythonExercise.finder.all()));
  }
  
  protected List<ExecutionResult> correct(String learnerSolution, PythonExercise exercise) {
    // FIXME: TEST!
    // List<CommitedTestData> userTestData = extractAndValidateTestData(form,
    // exercise);
    // TODO: evtl. Anzeige aussortiertes TestDaten?
    // userTestData =
    // userTestData.stream().filter(CommitedTestData::isOk).collect(Collectors.toList());
    // TODO: evt. Speichern der Lösung und Laden bei erneuter Bearbeitung?
    
    return CORRECTOR.correct(exercise, learnerSolution, new ArrayList<>(/* userTestData */));
  }
  
}
