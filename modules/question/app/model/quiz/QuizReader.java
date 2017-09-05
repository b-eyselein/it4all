package model.quiz;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseCollectionReader;
import model.question.Question;
import model.question.QuestionReader;
import play.data.DynamicForm;

public class QuizReader extends ExerciseCollectionReader<Question, Quiz> {

  public QuizReader() {
    super("question", QuestionReader.getInstance(), Quiz.finder, Quiz[].class);
  }

  public void initRemainingExFromForm(Quiz exercise, DynamicForm form) {
    exercise.setTheme(form.get("theme"));
  }

  @Override
  public Quiz instantiateExercise(int id) {
    return new Quiz(id);
  }

  @Override
  public void saveExercise(Quiz exercise) {
    exercise.save();
  }

  protected void updateExercise(Quiz exercise, JsonNode exerciseNode) {
    exercise.setTheme(exerciseNode.get("theme").asText());
  }

}
