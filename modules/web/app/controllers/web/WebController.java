package controllers.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import controllers.core.ExerciseController;
import model.StringConsts;
import model.WebExercise;
import model.WebSolution;
import model.WebSolutionKey;
import model.WebUser;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.result.WebResult;
import model.task.WebTask;
import model.user.User;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class WebController extends ExerciseController<WebExercise, WebResult> {
  
  public static final String HTML_TYPE = "html";
  public static final String JS_TYPE = "js";
  
  private static final String BASE_URL = "http://localhost:9000";
  
  public static final String STANDARD_HTML = "<!doctype html>\n<html>\n<head>\n  \n</head>\n<body>\n  \n</body>\n</html>";
  
  public static final String STANDARD_HTML_PLAYGROUND = "<!doctype html>\n<html>\n<head>\n"
      + "<style>\n/* Css-Anweisungen */\n\n</style>\n"
      + "<script type=\"text/javascript\">\n// Javascript-Code\n\n</script>\n"
      + "</head>\n<body>\n  <!-- Html-Elemente -->\n  \n</body>\n</html>";
  
  private static final List<String> ALLOWED_TYPES = Arrays.asList(HTML_TYPE, JS_TYPE);
  
  @Inject
  public WebController(FormFactory theFactory) {
    super(theFactory, "web", WebExercise.finder);
  }
  
  public static User getUser() {
    User user = ExerciseController.getUser();
    
    if(WebUser.finder.byId(user.name) == null)
      // Make sure there is a corresponding entrance in other db...
      new WebUser(user.name).save();
    
    return user;
  }
  
  private static List<WebResult> correct(String learnerSolution, WebExercise exercise, User user, boolean isJs) {
    saveSolution(learnerSolution, new WebSolutionKey(user.name, exercise.getId()));
    
    String solutionUrl = BASE_URL + routes.SolutionController.site(user.name, exercise.getId()).url();
    
    WebDriver driver = new HtmlUnitDriver(true);
    driver.get(solutionUrl);
    
    List<WebTask> tasks = isJs ? new ArrayList<>(exercise.jsTasks) : new ArrayList<>(exercise.htmlTasks);
    return tasks.stream().map(task -> task.evaluate(driver)).collect(Collectors.toList());
  }
  
  private static void saveSolution(String learnerSolution, WebSolutionKey key) {
    WebSolution solution = WebSolution.finder.byId(key);
    
    if(solution == null)
      solution = new WebSolution(key);
    
    solution.sol = learnerSolution;
    solution.save();
  }
  
  public Result correct(int id, String type) {
    if(!ALLOWED_TYPES.contains(type))
      return badRequest("Es gab einen internen Fehler!");
    
    boolean isJS = JS_TYPE.equals(type);
    User user = getUser();
    WebExercise exercise = WebExercise.finder.byId(id);
    
    String learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE);
    List<WebResult> result = correct(learnerSolution, exercise, user, isJS);
    log(user, new ExerciseCompletionEvent(request(), id, new LinkedList<>(result)));
    
    Html renderedResult = views.html.webResult.render(result);
    
    return ok(
        views.html.correction.render("Web", renderedResult, learnerSolution, user, routes.WebController.exercises()));
  }
  
  public Result correctLive(int id, String type) {
    if(!ALLOWED_TYPES.contains(type))
      return badRequest("Es gab einen internen Fehler!");
    
    boolean isJS = JS_TYPE.equals(type);
    User user = getUser();
    WebExercise exercise = WebExercise.finder.byId(id);
    
    String learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE);
    List<WebResult> result = correct(learnerSolution, exercise, user, isJS);
    log(user, new ExerciseCorrectionEvent(request(), id, new LinkedList<>(result)));
    
    return ok(views.html.webResult.render(result));
  }
  
  public Result exercise(int id, String type) {
    User user = getUser();
    
    if(!ALLOWED_TYPES.contains(type))
      return redirect(routes.WebController.index());
    
    log(user, new ExerciseStartEvent(request(), id));
    
    return ok(views.html.webExercise.render(user, WebExercise.finder.byId(id), type,
        SolutionController.getOldSolOrDefault(user.name, id), "Html-Korrektur"));
  }
  
  public Result exercises() {
    return ok(views.html.webExercises.render(getUser(), WebExercise.finder.all()));
  }
  
  public Result index() {
    return ok(views.html.webIndex.render(getUser(), WebExercise.finder.all()));
  }
  
  public Result playground() {
    return ok(views.html.webPlayground.render(getUser()));
  }
  
  @Override
  protected List<WebResult> correct(String learnerSolution, WebExercise exercise, User user) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected Html renderExercise(User user, WebExercise exercise) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  protected Html renderResult(List<WebResult> correctionResult) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
