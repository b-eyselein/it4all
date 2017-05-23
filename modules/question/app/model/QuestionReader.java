package model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import model.question.Answer;
import model.question.AnswerKey;
import model.question.Correctness;
import model.question.GivenAnswerQuestion;
import model.question.Question;
import play.libs.Json;

public class QuestionReader extends ExerciseReader<Question> {

  public QuestionReader() {
    super("choice");
  }

  private static Answer readAnswer(JsonNode answerNode) {
    AnswerKey key = Json.fromJson(answerNode.get(StringConsts.KEY_NAME), AnswerKey.class);

    Answer answer = Answer.finder.byId(key);
    if(answer == null)
      return Json.fromJson(answerNode, Answer.class);

    answer.correctness = Correctness.valueOf(answerNode.get("correctness").asText());
    answer.text = answerNode.get(StringConsts.TEXT_NAME).asText();
    return answer;
  }

  @Override
  public void saveExercise(Question exercise) {
    exercise.saveInDb();
  }

  private List<Answer> readAnswers(JsonNode answersNode) {
    return StreamSupport.stream(answersNode.spliterator(), true).map(QuestionReader::readAnswer)
        .collect(Collectors.toList());
  }

  @Override
  protected Question readExercise(JsonNode exerciseNode) {
    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();

    GivenAnswerQuestion question = GivenAnswerQuestion.finder.byId(id);
    if(question == null)
      question = new GivenAnswerQuestion(id);

    question.author = exerciseNode.get(StringConsts.AUTHOR_NAME).asText();
    question.title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    question.maxPoints = exerciseNode.get("maxPoints").asInt();
    question.text = exerciseNode.get(StringConsts.TEXT_NAME).asText();
    question.answers = readAnswers(exerciseNode.get("answers"));

    return question;
  }

}
