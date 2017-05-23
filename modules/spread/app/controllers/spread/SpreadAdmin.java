package controllers.spread;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.SpreadExercise;
import model.SpreadExerciseReader;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class SpreadAdmin extends AbstractAdminController<SpreadExercise, SpreadExerciseReader> {

  @Inject
  public SpreadAdmin(FormFactory theFactory) {
    super(theFactory, SpreadExercise.finder, "spread", new SpreadExerciseReader());
  }

  @Override
  public Result index() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result newExercise() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Result newExerciseForm() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Html renderCreated(List<SpreadExercise> created) {
    return views.html.spreadcreation.render(created);
  }

  @Override
  public Result uploadForm() {
    return ok(views.html.spreadupload.render(getUser()));
  }

}
