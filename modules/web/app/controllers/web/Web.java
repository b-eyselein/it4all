package controllers.web;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import controllers.core.ExerciseController;
import model.Util;
import model.WebExercise;
import model.WebSolution;
import model.WebSolutionKey;
import model.WebUser;
import model.logging.ExerciseCompletionEvent;
import model.logging.ExerciseCorrectionEvent;
import model.logging.ExerciseStartEvent;
import model.result.ElementResult;
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
  
  private static final List<String> ALLOWED_TYPES = Arrays.asList("html", "css", "js");
  
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
  
  private static List<ElementResult> correct(String learnerSolution, String username, String type,
      WebExercise exercise) {
    saveSolution(learnerSolution, new WebSolutionKey(username, exercise.id));
    
    String solutionUrl = "http://localhost:9000" + routes.Solution.site(username, exercise.id).url();
    
    WebDriver driver = new HtmlUnitDriver(true);
    driver.get(solutionUrl);
    
    return exercise.getTasks(type).stream().map(task -> task.evaluate(driver)).collect(Collectors.toList());
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
  
  public Result correct(int id, String type) {
    User user = getUser();
    WebExercise exercise = WebExercise.finder.byId(id);
    
    if(!typeIsCorrect(type))
      return badRequest(views.html.error.render(user, "Der Korrekturtyp wurde nicht korrekt spezifiziert!"));
    
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);
    
    List<ElementResult> result = correct(learnerSolution, user.name, type, exercise);
    
    log(user, new ExerciseCompletionEvent(request(), id, new LinkedList<>(result)));
    return ok(views.html.correction.render("Web",
        "js".equals(type) ? views.html.webresult.render(result) : views.html.jsResult.render(result), learnerSolution,
        getUser()));
  }
  
  public Result correctLive(int id, String type) {
    User user = getUser();
    WebExercise exercise = WebExercise.finder.byId(id);
    
    if(!typeIsCorrect(type))
      return badRequest(views.html.error.render(user, "Der Korrekturtyp wurde nicht korrekt spezifiziert!"));
    
    DynamicForm form = factory.form().bindFromRequest();
    String learnerSolution = form.get(LEARNER_SOLUTION_VALUE);
    
    List<ElementResult> result = correct(learnerSolution, user.name, type, exercise);
    
    log(user, new ExerciseCorrectionEvent(request(), id, new LinkedList<>(result)));
    
    if("js".equals(type))
      return ok(views.html.jsResult.render(result));
    else
      return ok(views.html.webresult.render(result));
  }
  
  public Result exercise(int id, String type) {
    User user = getUser();
    WebExercise exercise = WebExercise.finder.byId(id);
    
    if(!typeIsCorrect(type) || exercise == null)
      return redirect(controllers.web.routes.Web.index());
    
    WebSolutionKey key = new WebSolutionKey(user.name, id);
    WebSolution sol = WebSolution.finder.byId(key);
    String defaultOrOldSolution = sol == null ? STANDARD_HTML : sol.solution;
    
    log(user, new ExerciseStartEvent(request(), id));
    
    return ok(views.html.webExercise.render(user, exercise, type, defaultOrOldSolution, "Html-Korrektur"));
  }
  
  public Result exercises() {
    return ok(views.html.exercises.render(getUser(), WebExercise.finder.all()));
  }
  
  public Result index() {
    return ok(views.html.weboverview.render(getUser(), WebExercise.finder.all()));
  }
  
  public Result playground() {
    return ok(views.html.playground.render(getUser()));
  }
  
}
