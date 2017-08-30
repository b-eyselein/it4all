package controllers.ebnf;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AExerciseAdminController;
import model.ebnf.EBNFExercise;
import model.ebnf.EBNFExerciseReader;
import model.user.User;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class EBNFAdminController extends AExerciseAdminController<EBNFExercise> {

  @Inject
  public EBNFAdminController(FormFactory theFactory) {
    super(theFactory, EBNFExercise.finder(), new EBNFExerciseReader());
  }

  @Override
  public Result index() {
    return ok(views.html.ebnf.ebnfAdmin.render(getUser()));
  }

  @Override
  public Result newExerciseForm() {
    return ok("TODO!");
  }

  protected Html renderCreated(List<EBNFExercise> created) {
    return new Html("TODO!");
  }

  protected Html renderExercises(User user, List<EBNFExercise> allExercises) {
    return new Html("TODO!");
  }

}