package controllers.web;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import controllers.core.ExerciseController;
import model.StringConsts;
import model.Util;
import model.WebExercise;
import model.WebSolution;
import model.WebSolutionKey;
import model.WebUser;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.result.ElementResult;
import model.result.JsWebResult;
import model.user.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

public class Web extends ExerciseController {
  
  private static final String STANDARD_HTML = "<!doctype html>\n<html>\n\n<head>\n</head>\n\n<body>\n</body>\n\n</html>";
  
  public static final String STANDARD_HTML_PLAYGROUND = "<!doctype html>\n<html>\n<head>\n"
      + "<style>\n/* Css-Anweisungen */\n\n</style>\n"
      + "<script type=\"text/javascript\">\n// Javascript-Code\n\n</script>\n"
      + "</head>\n<body>\n  <!-- Html-Elemente -->\n  \n</body>\n</html>";
  
  private static final List<String> ALLOWED_TYPES = Arrays.asList("html", "js");
  
  @Inject
  public Web(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public static User getUser() {
    User user = ExerciseController.getUser();
    
    if(WebUser.finder.byId(user.name) == null)
      // Make sure there is a corresponding entrance in other db...
      new WebUser(user.name).save();
    
    return user;
  }
  
  private static List<JsWebResult> correctJs(String learnerSolution, String username, WebExercise exercise) {
    saveSolution(learnerSolution, new WebSolutionKey(username, exercise.id));
    
    String solutionUrl = "http://localhost:9000" + routes.Solution.site(username, exercise.id).url();
    
    WebDriver driver = new HtmlUnitDriver(true);
    driver.get(solutionUrl);
    
    return exercise.jsTasks.stream().map(task -> task.evaluate(driver)).collect(Collectors.toList());
  }
  
  private static List<ElementResult> correctWeb(String learnerSolution, String username, WebExercise exercise) {
    saveSolution(learnerSolution, new WebSolutionKey(username, exercise.id));
    
    String solutionUrl = "http://localhost:9000" + routes.Solution.site(username, exercise.id).url();
    
    WebDriver driver = new HtmlUnitDriver(true);
    driver.get(solutionUrl);
    
    return exercise.htmlTasks.stream().map(task -> task.evaluate(driver)).collect(Collectors.toList());
  }
  
  private static void saveSolution(String learnerSolution, WebSolutionKey key) {
    WebSolution solution = WebSolution.finder.byId(key);
    
    if(solution == null)
      solution = new WebSolution(key);
    
    solution.solution = learnerSolution;
    solution.save();
  }
  
  private static boolean typeIsCorrect(String type) {
    return ALLOWED_TYPES.contains(type);
  }
  
  public Result correctJs(int id) {
    User user = getUser();
    WebExercise exercise = WebExercise.finder.byId(id);
    
    String learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE);
    
    List<JsWebResult> result = correctJs(learnerSolution, user.name, exercise);
    
    log(user, new ExerciseCompletionEvent(request(), id, new LinkedList<>(result)));
    return ok(views.html.correction.render("Web", views.html.jsResult.render(result), learnerSolution, getUser()));
  }
  
  public Result correctJsLive(int id) {
    User user = getUser();
    WebExercise exercise = WebExercise.finder.byId(id);
    
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(StringConsts.FORM_VALUE);
    
    List<JsWebResult> result = correctJs(learnerSolution, user.name, exercise);
    
    log(user, new ExerciseCorrectionEvent(request(), id, new LinkedList<>(result)));
    
    return ok(views.html.jsResult.render(result));
  }
  
  public Result correctWeb(int id) {
    User user = getUser();
    WebExercise exercise = WebExercise.finder.byId(id);
    
    String learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE);
    
    List<ElementResult> result = correctWeb(learnerSolution, user.name, exercise);
    
    log(user, new ExerciseCompletionEvent(request(), id, new LinkedList<>(result)));
    return ok(views.html.correction.render("Web", views.html.webResult.render(result), learnerSolution, getUser()));
  }
  
  public Result correctWebLive(int id) {
    User user = getUser();
    WebExercise exercise = WebExercise.finder.byId(id);
    
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(StringConsts.FORM_VALUE);
    
    List<ElementResult> result = correctWeb(learnerSolution, user.name, exercise);
    
    log(user, new ExerciseCorrectionEvent(request(), id, new LinkedList<>(result)));
    
    return ok(views.html.webResult.render(result));
  }
  
  public Result exercise(int id, String type) {
    User user = getUser();
    
    if(!typeIsCorrect(type))
      return redirect(routes.Web.index());
    
    WebSolutionKey key = new WebSolutionKey(user.name, id);
    WebSolution sol = WebSolution.finder.byId(key);
    String defaultOrOldSolution = sol == null ? STANDARD_HTML : sol.solution;
    
    log(user, new ExerciseStartEvent(request(), id));
    
    return ok(
        views.html.webExercise.render(user, WebExercise.finder.byId(id), type, defaultOrOldSolution, "Html-Korrektur"));
  }
  
  public Result exercises() {
    return ok(views.html.webExercises.render(getUser(), WebExercise.finder.all()));
  }
  
  public Result index() {
    return ok(views.html.webIndex.render(getUser(), WebExercise.finder.all()));
  }
  
  public Result playground() {
    return ok(views.html.playground.render(getUser()));
  }
  
}
