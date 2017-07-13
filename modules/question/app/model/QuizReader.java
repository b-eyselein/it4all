package model;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;

public class QuizReader extends ExerciseReader<Quiz> {

  public QuizReader() {
    super("question");
  }

  @Override
  public void saveRead(Quiz exercise) {
    exercise.save();
  }

  @Override
  protected Quiz read(JsonNode exerciseNode) {
    return null;
  }

}
