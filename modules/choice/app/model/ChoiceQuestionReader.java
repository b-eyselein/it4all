package model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.Correctness;
import model.QuestionType;
import model.exercisereading.ExerciseReader;

public class ChoiceQuestionReader extends ExerciseReader<ChoiceQuestion> {

  public ChoiceQuestionReader() {
    super("choice");
  }

  private static ChoiceQuestion readChoiceQuestion(int id, QuestionType type, String title, String text,
      List<ChoiceAnswer> answers) {
    ChoiceQuestion question = ChoiceQuestion.finder.byId(id);

    if(question == null)
      question = new ChoiceQuestion(id);

    question.questionType = type;
    question.title = title;
    question.text = text;
    question.answers = answers;

    return question;
  }

  private ChoiceAnswer readAnswer(JsonNode answerNode, int questionId) {
    JsonNode idNode = answerNode.get(ID_NAME);
    JsonNode correctnessNode = answerNode.get("correctness");
    JsonNode textNode = answerNode.get(TEXT_NAME);

    ChoiceAnswerKey key = new ChoiceAnswerKey(idNode.asInt(), questionId);

    ChoiceAnswer answer = ChoiceAnswer.finder.byId(key);
    if(answer == null)
      answer = new ChoiceAnswer(key);

    answer.correctness = Correctness.valueOf(correctnessNode.asText());
    answer.text = textNode.asText();

    return answer;
  }

  private List<ChoiceAnswer> readAnswers(JsonNode answersNode, int questionId) {
    List<ChoiceAnswer> answers = new ArrayList<>(answersNode.size());
    for(JsonNode answerNode: answersNode)
      answers.add(readAnswer(answerNode, questionId));
    return answers;
  }

  @Override
  protected ChoiceQuestion readExercise(JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get(ID_NAME);
    JsonNode typeNode = exerciseNode.get("type");

    JsonNode titleNode = exerciseNode.get(TITLE_NAME);
    JsonNode textNode = exerciseNode.get(TEXT_NAME);
    JsonNode answersNode = exerciseNode.get("answers");

    QuestionType type = QuestionType.valueOf(typeNode.asText());

    int id = idNode.asInt();
    String title = titleNode.asText();
    String text = textNode.asText();
    List<ChoiceAnswer> answers = readAnswers(answersNode, id);

    return readChoiceQuestion(id, type, title, text, answers);
  }

}
