package model.question;

import com.fasterxml.jackson.databind.JsonNode;

import model.StringConsts;
import model.exercisereading.ExerciseReader;
import play.data.DynamicForm;
import play.libs.Json;

public class QuestionReader extends ExerciseReader<Question> {
  
  private static final QuestionReader INSTANCE = new QuestionReader();
  
  private QuestionReader() {
    super("question", Question.finder, Question[].class);
  }
  
  public static QuestionReader getInstance() {
    return INSTANCE;
  }
  
  public static Answer readAnswer(JsonNode answerNode) {
    AnswerKey key = Json.fromJson(answerNode.get(StringConsts.KEY_NAME), AnswerKey.class);
    
    Answer answer = Answer.finder.byId(key);
    if(answer == null)
      answer = new Answer(key);
    
    answer.correctness = Correctness.valueOf(answerNode.get("correctness").asText());
    answer.text = readTextArray(answerNode.get(StringConsts.TEXT_NAME), "");
    return answer;
  }
  
  @Override
  public void initRemainingExFromForm(Question exercise, DynamicForm form) {
    exercise.setMaxPoints(Integer.parseInt(form.get(StringConsts.MAX_POINTS)));
    exercise.setQuestionType(Question.QType.valueOf(form.get(StringConsts.EXERCISE_TYPE)));
    // exercise.setAnswers(readArray(form.get("answers"),
    // QuestionReader::readAnswer));
  }
  
  @Override
  public void saveRead(Question exercise) {
    exercise.saveInDb();
  }
  
  @Override
  protected Question instantiateExercise(int id) {
    return new Question(id);
  }
  
  @Override
  protected void updateExercise(Question exercise, JsonNode exerciseNode) {
    exercise.setMaxPoints(exerciseNode.get(StringConsts.MAX_POINTS).asInt());
    exercise.setQuestionType(Question.QType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText()));
    exercise.setAnswers(readArray(exerciseNode.get("answers"), QuestionReader::readAnswer));
  }
  
}
