package controllers.programming;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.ProgExercise;
import model.ProgExerciseReader;
import model.Util;
import model.exercisereading.AbstractReadingResult;
import model.exercisereading.ReadingError;
import model.exercisereading.ReadingResult;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;

public class ProgAdmin extends AbstractAdminController<ProgExercise, ProgExerciseReader> {
  
  @Inject
  public ProgAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, "prog", new ProgExerciseReader());
  }
  
  public Result export() {
    String exported = Json.prettyPrint(Json.toJson(ProgExercise.finder.all()));
    return ok(views.html.export.render(getUser(), exported));
  }
  
  public Result index() {
    return ok(views.html.progAdmin.progAdmin.render(getUser()));
  }
  
  @Override
  public Result readStandardExercises() {
    AbstractReadingResult<ProgExercise> abstractResult = exerciseReader.readStandardExercises();
    
    if(!abstractResult.isSuccess())
      return badRequest(views.html.jsonReadingError.render(getUser(), (ReadingError<ProgExercise>) abstractResult));
    
    ReadingResult<ProgExercise> result = (ReadingResult<ProgExercise>) abstractResult;
    
    saveExercises(result.getRead());
    return ok(views.html.preview.render(getUser(), views.html.progAdmin.progCreation.render(result.getRead())));
  }
  
  @Override
  public Result uploadFile() {
    // TODO Auto-generated method stub
    return ok("TODO");
  }
  
  @Override
  public Result uploadForm() {
    // TODO Auto-generated method stub
    return ok("TODO");
  }
  
  @Override
  protected void saveExercises(List<ProgExercise> exercises) {
    // TODO Auto-generated method stub
    exercises.forEach(ProgExercise::save);
  }
  
}
