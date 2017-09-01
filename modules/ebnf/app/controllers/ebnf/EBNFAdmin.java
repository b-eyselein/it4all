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

public class EBNFAdmin extends AExerciseAdminController<EBNFExercise> {
  
  @Inject
  public EBNFAdmin(FormFactory theFactory) {
    super(theFactory, EBNFRoutesObject$.MODULE$, EBNFExercise.finder(), new EBNFExerciseReader());
  }
  
  @Override
  public Result adminIndex() {
    return ok(views.html.ebnfAdmin.index.render(getUser()));
  }
  
  protected Html renderCreated(List<EBNFExercise> exercises) {
    return views.html.ebnfAdmin.ebnfCreation.render(exercises);
  }
  
  @Override
  protected Html renderExEditForm(User user, EBNFExercise exercise, boolean isCreation) {
    return views.html.ebnfAdmin.newExForm.render(getUser(), exercise);
  }
  
  protected Html renderExercises(List<EBNFExercise> exercises) {
    return views.html.ebnfAdmin.ebnfCreation.render(exercises);
  }
  
}