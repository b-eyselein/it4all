package controllers.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.StringConsts;
import model.XmlExType;
import model.XmlExercise;
import model.XmlExerciseReader;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class XmlAdmin extends AbstractAdminController<XmlExercise, XmlExerciseReader> {

  @Inject
  public XmlAdmin(FormFactory theFactory) {
    super(theFactory, XmlExercise.finder, "xml", new XmlExerciseReader());
  }

  @Override
  public XmlExercise getNew(int id) {
    return new XmlExercise(id);
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

  @Override
  protected void initRemainingExFromForm(DynamicForm form, XmlExercise exercise) {
    exercise.exerciseType = XmlExType.valueOf(form.get("exerciseType"));
    exercise.fixedStart = form.get("fixedStart");
    exercise.referenceFileName = form.get("referenceFileName");

    List<String> referenceFileContent = Arrays.asList(form.get("referenceFileContent").split("\n"));
    Path referenceFilePath = Paths.get(getSampleDir().toString(),
        exercise.referenceFileName + "." + exercise.getReferenceFileEnding());
    try {
      Files.write(referenceFilePath, referenceFileContent, StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      Logger.error("There has been an error creating a sample xml file", e);
    }
  }

}
