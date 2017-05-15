package controllers.questions;

import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.Quiz;
import model.Util;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

public class QuizAdmin extends AbstractAdminController<Quiz, QuizReader> {
  
  @Inject
  public QuizAdmin(Util theUtil, FormFactory theFactory, String theExerciseType) {
    super(theUtil, theFactory, Quiz.finder, theExerciseType, new QuizReader());
    // TODO Auto-generated constructor stub
  }
  
  @Override
  public Html renderCreated(List<Quiz> created) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Result uploadFile() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public Result uploadForm() {
    // TODO Auto-generated method stub
    return null;
  }
  
}
