package controllers.questions;

import com.fasterxml.jackson.databind.JsonNode;

import model.Quiz;
import model.exercisereading.ExerciseReader;

public class QuizReader extends ExerciseReader<Quiz> {

  public QuizReader() {
    super("question");
  }

  @Override
  public void saveExercise(Quiz exercise) {
    // TODO Auto-generated method stub
    exercise.save();
  }

  @Override
  protected Quiz readExercise(JsonNode exerciseNode) {
    // TODO Auto-generated method stub
    return null;
  }

}
