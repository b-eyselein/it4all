package model;

import java.util.ArrayList;
import java.util.List;

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

  private Answer readAnswer(JsonNode answerNode) {
    AnswerKey key = Json.fromJson(answerNode.get(StringConsts.KEY_NAME), AnswerKey.class);

    Answer answer = Answer.finder.byId(key);
    if(answer == null)
      return Json.fromJson(answerNode, Answer.class);

    answer.correctness = Correctness.valueOf(answerNode.get("correctness").asText());
    answer.text = answerNode.get(StringConsts.TEXT_NAME).asText();
    return answer;
  }

  private List<Answer> readAnswers(JsonNode answersNode) {
    List<Answer> answers = new ArrayList<>(answersNode.size());

    for(JsonNode answerNode: answersNode)
      answers.add(readAnswer(answerNode));

    return answers;
  }

  @Override
  protected Question readExercise(JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get(StringConsts.ID_NAME);
    JsonNode titleNode = exerciseNode.get(StringConsts.TITLE_NAME);
    JsonNode authorNode = exerciseNode.get(StringConsts.AUTHOR_NAME);
    JsonNode pointsNode = exerciseNode.get("maxPoints");
    JsonNode textNode = exerciseNode.get(StringConsts.TEXT_NAME);
    JsonNode answersNode = exerciseNode.get("answers");

    int id = idNode.asInt();

    GivenAnswerQuestion question = GivenAnswerQuestion.finder.byId(id);
    if(question == null)
      question = new GivenAnswerQuestion(id);

    question.author = authorNode.asText();
    question.title = titleNode.asText();
    question.maxPoints = pointsNode.asInt();
    question.text = textNode.asText();
    question.answers = readAnswers(answersNode);

    return question;
  }

}
