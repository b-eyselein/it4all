package controllers.xml;

import javax.inject.Inject;

import controllers.core.AExerciseAdminController;
import model.XmlExercise;
import model.XmlExerciseReader;
import model.user.User;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class XmlAdmin extends AExerciseAdminController<XmlExercise> {
  
  @Inject
  public XmlAdmin(FormFactory theFactory) {
    super(theFactory, XmlRoutesObject$.MODULE$, XmlExercise.finder, XmlExerciseReader.getInstance());
  }
  
  @Override
  public Result index() {
    return ok(views.html.xmlAdmin.index.render(getUser()));
  }
  
  @Override
  protected Html renderExEditForm(User user, XmlExercise exercise, boolean isCreation) {
    return views.html.xmlAdmin.editExForm.render(user, exercise, isCreation);
  }
  
}
