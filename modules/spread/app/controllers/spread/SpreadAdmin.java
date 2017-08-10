package controllers.spread;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AExerciseAdminController;
import model.SpreadExercise;
import model.SpreadExerciseReader;
import model.StringConsts;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class SpreadAdmin extends AExerciseAdminController<SpreadExercise> {

  @Inject
  public SpreadAdmin(FormFactory theFactory) {
    super(theFactory, SpreadExercise.finder, SpreadExerciseReader.getInstance());
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
  protected SpreadExercise initRemainingExFromForm(int id, String title, String author, String text, DynamicForm form) {
    String templateFilename = form.get(StringConsts.TEMPALTE_FILENAME);
    String sampleFilename = form.get(StringConsts.SAMPLE_FILENAME);

    return new SpreadExercise(id, title, author, text, sampleFilename, templateFilename);
  }

}
