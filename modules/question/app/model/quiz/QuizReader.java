package model.quiz;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseCollectionReader;
import model.question.Question;
import model.question.QuestionReader;

public class QuizReader extends ExerciseCollectionReader<Question, Quiz> {

  public QuizReader() {
    super(QuestionReader.getInstance(), Quiz.finder, Quiz[].class);
  }

  @Override
  public void saveRead(Quiz exercise) {
    exercise.save();
  }
  
  @Override
  protected Quiz instantiateExercise(int id, String title, String author, String text, JsonNode exerciseNode) {
    // TODO Auto-generated method stub
    String theme = exerciseNode.get("theme").asText();
    
    return new Quiz(id, title, author, text, theme);
  }
  
}
