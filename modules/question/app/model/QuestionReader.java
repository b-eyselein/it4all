package model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import play.libs.Json;

public class QuestionReader extends ExerciseReader<Question> {

  public QuestionReader() {
    super("choice");
  }

  private Answer readAnswer(JsonNode answerNode) {
    AnswerKey key = Json.fromJson(answerNode.get(KEY_NAME), AnswerKey.class);

    Answer answer = Answer.finder.byId(key);
    if(answer == null)
      return Json.fromJson(answerNode, Answer.class);

    answer.correctness = Correctness.valueOf(answerNode.get("correctness").asText());
    answer.text = answerNode.get(TEXT_NAME).asText();
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
    JsonNode idNode = exerciseNode.get(ID_NAME);
    JsonNode titleNode = exerciseNode.get(TITLE_NAME);
    JsonNode authorNode = exerciseNode.get("author");
    JsonNode pointsNode = exerciseNode.get("maxPoints");
    JsonNode textNode = exerciseNode.get(TEXT_NAME);
    JsonNode questionTypeNode = exerciseNode.get("questionType");
    JsonNode answersNode = exerciseNode.get("answers");

    int id = idNode.asInt();

    Question question = Question.finder.byId(id);
    if(question == null)
      question = new Question(id);

    question.author = authorNode.asText();
    question.title = titleNode.asText();
    question.maxPoints = pointsNode.asInt();
    question.text = textNode.asText();
    question.questionType = QuestionType.valueOf(questionTypeNode.asText());
    question.answers = readAnswers(answersNode);

    return question;
  }

}
