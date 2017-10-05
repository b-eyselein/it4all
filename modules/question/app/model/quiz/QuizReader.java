package model.quiz;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseCollectionReader;
import model.question.Question;
import play.data.DynamicForm;

public class QuizReader extends ExerciseCollectionReader<Question, Quiz> {

  public QuizReader() {
    super("question", Quiz.finder, Quiz[].class);
  }

  public void initRemainingExFromForm(Quiz exercise, DynamicForm form) {
    exercise.setTheme(form.get("theme"));
  }

  @Override
  public Quiz instantiate(int id) {
    return new Quiz(id);
  }

  @Override
  public void updateCollection(Quiz coll, JsonNode node) {
    // TODO Auto-generated method stub

  }

  protected void updateExercise(Quiz exercise, JsonNode exerciseNode) {
    exercise.setTheme(exerciseNode.get("theme").asText());
  }

}
