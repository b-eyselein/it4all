package model.question;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.JsonWrapper;
import model.StringConsts;
import model.exercisereading.ExerciseReader;
import play.libs.Json;

public class QuestionReader extends ExerciseReader<Question> {

  private static final QuestionReader INSTANCE = new QuestionReader();

  private QuestionReader() {
    super("question");
  }

  public static QuestionReader getInstance() {
    return INSTANCE;
  }

  private static Answer readAnswer(JsonNode answerNode) {
    AnswerKey key = Json.fromJson(answerNode.get(StringConsts.KEY_NAME), AnswerKey.class);

    Answer answer = Answer.finder.byId(key);
    if(answer == null)
      answer = new Answer(key);

    answer.correctness = Correctness.valueOf(answerNode.get("correctness").asText());
    answer.text = JsonWrapper.readTextArray(answerNode.get(StringConsts.TEXT_NAME), "");
    return answer;
  }

  @Override
  public void saveRead(Question exercise) {
    exercise.saveInDb();
  }

  @Override
  protected Question read(JsonNode exerciseNode) {
    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();

    String title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    String author = exerciseNode.get(StringConsts.AUTHOR_NAME).asText();
    String text = JsonWrapper.readTextArray(exerciseNode.get(StringConsts.TEXT_NAME), "");
    int maxPoints = exerciseNode.get(StringConsts.MAX_POINTS).asInt();
    Question.QType questionType = Question.QType.valueOf(exerciseNode.get(StringConsts.EXERCISE_TYPE).asText());
    List<Answer> answers = readArray(exerciseNode.get("answers"), QuestionReader::readAnswer);

    Question question = Question.finder.byId(id);
    if(question == null)
      return new Question(id, title, author, text, maxPoints, questionType, answers);
    else
      return question.updateValues(id, title, author, text, maxPoints, questionType, answers);
  }

}
