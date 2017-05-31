package controllers.web;

import model.WebSolution;
import model.WebSolutionKey;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;

public class SolutionController extends Controller {
  
  public static final String STANDARD_HTML = "<!doctype html>\n<html>\n<head>\n  \n</head>\n<body>\n  \n</body>\n</html>";
  
  public static String getOldSolOrDefault(String userName, int exerciseId) {
    WebSolutionKey key = new WebSolutionKey(userName, exerciseId);
    WebSolution sol = WebSolution.finder.byId(key);
    return sol == null ? STANDARD_HTML : sol.sol;
  }
  
  public Result site(String username, int exerciseId) {
    return ok(new Html(getOldSolOrDefault(username, exerciseId)));
  }
  
}
