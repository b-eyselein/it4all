package controllers.programming;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.ProgExercise;
import model.ProgExerciseReader;
import model.Util;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class ProgAdmin extends AbstractAdminController<ProgExercise, ProgExerciseReader> {
  
  @Inject
  public ProgAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, ProgExercise.finder, "prog", new ProgExerciseReader());
  }
  
  public Result index() {
    return ok(views.html.progAdmin.index.render(getUser()));
  }
  
  @Override
  public Html renderCreated(List<ProgExercise> exercises) {
    return views.html.progAdmin.progCreation.render(exercises);
  }
  
  @Override
  public Result uploadFile() {
    // TODO Auto-generated method stub
    return ok("TODO");
  }
  
  @Override
  public Result uploadForm() {
    // TODO Auto-generated method stub
    return ok("TODO");
  }
  
}
