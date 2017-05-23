package controllers.xml;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.XmlExercise;
import model.XmlExerciseReader;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class XmlAdmin extends AbstractAdminController<XmlExercise, XmlExerciseReader> {

  @Inject
  public XmlAdmin(FormFactory theFactory) {
    super(theFactory, XmlExercise.finder, "xml", new XmlExerciseReader());
  }

  @Override
  public Result index() {
    return ok(views.html.xmlAdmin.index.render(getUser()));
  }

  @Override
  public Result newExercise() {
    return ok("TODO!");
  }

  @Override
  public Result newExerciseForm() {
    return ok(views.html.xmlAdmin.newExerciseForm.render(getUser()));
  }

  @Override
  public Html renderCreated(List<XmlExercise> exercises) {
    return views.html.xmlAdmin.xmlCreation.render(exercises);
  }

  @Override
  public Result uploadForm() {
    return ok(views.html.xmlupload.render(getUser()));
  }

}
