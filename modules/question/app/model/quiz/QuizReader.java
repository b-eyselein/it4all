package model.quiz;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseCollectionReader;
import model.question.Question;
import model.question.QuestionReader;

public class QuizReader extends ExerciseCollectionReader<Question, Quiz> {

  public QuizReader() {
    super(QuestionReader.getInstance());
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
