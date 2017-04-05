package controllers.mindmap;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.Secured;
import model.Util;
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class Mindmap extends ExerciseController {
  
  @Inject
  public Mindmap(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public Result index() {
    return ok(views.html.mindmapindex.render(getUser()));
  }

  public Result upload() {
    // TODO: getFile, correct and present for download!
    return ok(views.html.mindmapcorrect.render(getUser()));
  }

}
