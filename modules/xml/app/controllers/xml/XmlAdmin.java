package controllers.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AExerciseAdminController;
import model.StringConsts;
import model.XmlExType;
import model.XmlExercise;
import model.XmlExerciseReader;
import play.Logger;
import play.data.DynamicForm;
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
  
  @Override
  protected XmlExercise initRemainingExFromForm(int id, String title, String author, String text, DynamicForm form) {
    String fixedStart = form.get(StringConsts.FIXED_START);
    XmlExType exerciseType = XmlExType.valueOf(form.get(StringConsts.EXERCISE_TYPE));
    String referenceFileName = form.get(StringConsts.REFERENCE_FILE_NAME);
    
    XmlExercise exercise = new XmlExercise(id, title, author, text, fixedStart, exerciseType, referenceFileName);
    
    Path referenceFilePath = Paths.get(getSampleDir().toString(),
        exercise.getReferenceFileName() + "." + exercise.getReferenceFileEnding());
    List<String> referenceFileContent = Arrays
        .asList(form.get(StringConsts.REFERENCE_FILE_CONTENT).split(StringConsts.NEWLINE));
    
    try {
      Files.write(referenceFilePath, referenceFileContent, StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      Logger.error("There has been an error creating a sample xml file", e);
    }
    
    return exercise;
  }
  
}
