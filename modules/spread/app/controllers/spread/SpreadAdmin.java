package controllers.spread;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.SpreadExercise;
import model.SpreadExerciseReader;
import play.api.mvc.Call;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class SpreadAdmin extends AbstractAdminController<SpreadExercise, SpreadExerciseReader> {
  
  @Inject
  public SpreadAdmin(FormFactory theFactory) {
    super(theFactory, SpreadExercise.finder, "spread", new SpreadExerciseReader());
  }
  
  @Override
  public SpreadExercise getNew(int id) {
    return new SpreadExercise(id);
  }
  
  @Override
  public Result index() {
    return ok(views.html.spreadAdmin.index.render(getUser()));
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
  protected Call getIndex() {
    return controllers.spread.routes.SpreadAdmin.index();
  }
  
  @Override
  protected void initRemainingExFromForm(DynamicForm form, SpreadExercise exercise) {
    // TODO Auto-generated method stub
    
  }
  
}
