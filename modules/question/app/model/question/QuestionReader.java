package model.question;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.StringConsts;
import model.exercisereading.ExerciseReader;
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
  public void saveRead(Question exercise) {
    exercise.saveInDb();
  }

  @Override
  protected Question instantiateExercise(int id, String title, String author, String text, JsonNode exerciseNode) {
    int maxPoints = exerciseNode.get(StringConsts.MAX_POINTS).asInt();
    Question.QType questionType = Question.QType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText());
    List<Answer> answers = readArray(exerciseNode.get("answers"), QuestionReader::readAnswer);

    return new Question(id, title, author, text, maxPoints, questionType, answers);
  }

}
