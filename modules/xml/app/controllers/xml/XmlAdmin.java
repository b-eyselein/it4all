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
  public Result index() {
    return ok(views.html.xmlAdmin.index.render(getUser()));
  }

  @Override
  public Result newExercise() {
    DynamicForm form = factory.form().bindFromRequest();

    String title = form.get(StringConsts.TITLE_NAME);

    XmlExercise exercise = XmlExercise.finder.where().eq(StringConsts.TITLE_NAME, title).findUnique();
    if(exercise == null)
      exercise = new XmlExercise(findMinimalNotUsedId(finder));

    // TODO: evtl. author, title, text auslagern, da für alle Aufgaben gleich?
    exercise.author = form.get(StringConsts.AUTHOR_NAME);
    exercise.text = form.get(StringConsts.TEXT_NAME);
    exercise.title = form.get(StringConsts.TITLE_NAME);

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
      return badRequest(e.getMessage());
    }

    exercise.save();

    return ok(views.html.preview.render(getUser(), renderCreated(Arrays.asList(exercise))));
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
