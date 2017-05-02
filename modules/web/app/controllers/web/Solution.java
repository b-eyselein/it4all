package controllers.web;

import model.WebSolution;
import model.WebSolutionKey;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;

public class Solution extends Controller {

  public Result site(String username, int exerciseId) {
    WebSolution solution = WebSolution.finder.byId(new WebSolutionKey(username, exerciseId));

    if(solution == null)
      return badRequest("Fehler beim Laden der URL: Lösung ist nicht vorhanden!");

    return ok(new Html(solution.solution));
  }

}
