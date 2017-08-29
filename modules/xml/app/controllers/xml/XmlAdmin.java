package controllers.xml;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AExerciseAdminController;
import model.XmlExercise;
import model.XmlExerciseReader;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class XmlAdmin extends AExerciseAdminController<XmlExercise> {

  @Inject
  public XmlAdmin(FormFactory theFactory) {
    super(theFactory, XmlExercise.finder, XmlExerciseReader.getInstance());
  }

  @Override
  public Result index() {
    return ok(views.html.xmlAdmin.index.render(getUser()));
  }

  @Override
  public Result newExerciseForm() {
    return ok(views.html.xmlAdmin.newExerciseForm.render(getUser()));
  }

  @Override
  public Html renderCreated(List<XmlExercise> exercises) {
    return views.html.xmlAdmin.xmlCreation.render(exercises);
  }

}
