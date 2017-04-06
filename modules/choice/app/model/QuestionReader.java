package model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.Correctness;
import model.QuestionType;
import model.exercisereading.ExerciseReader;

public class QuestionReader extends ExerciseReader<Question> {

  public QuestionReader() {
    super("choice");
  }

  private Answer readAnswer(JsonNode answerNode) {
    JsonNode keyNode = answerNode.get("key");
    JsonNode correctnessNode = answerNode.get("correctness");
    JsonNode textNode = answerNode.get(TEXT_NAME);

    AnswerKey key = readKey(keyNode);

    Answer answer = Answer.finder.byId(key);
    if(answer == null)
      answer = new Answer(key);

    answer.correctness = Correctness.valueOf(correctnessNode.asText());
    answer.text = textNode.asText();

    return answer;
  }

  private List<Answer> readAnswers(JsonNode answersNode) {
    List<Answer> answers = new ArrayList<>(answersNode.size());

    for(JsonNode answerNode: answersNode)
      answers.add(readAnswer(answerNode));

    return answers;
  }

  private AnswerKey readKey(JsonNode keyNode) {
    JsonNode idNode = keyNode.get(ID_NAME);
    JsonNode questionIdNode = keyNode.get("questionId");

    return new AnswerKey(questionIdNode.asInt(), idNode.asInt());
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
