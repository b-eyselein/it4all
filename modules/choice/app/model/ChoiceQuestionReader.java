package model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.ChoiceAnswer.Correctness;
import model.exercisereading.ExerciseReader;

public class ChoiceQuestionReader extends ExerciseReader<ChoiceQuestion> {

  public ChoiceQuestionReader() {
    super("choice");
  }

  private static MCQuestion readMCQuestion(int id, String title, String text, List<ChoiceAnswer> answers) {
    MCQuestion question = MCQuestion.finder.byId(id);

    if(question == null)
      question = new MCQuestion(id);

    question.title = title;
    question.text = text;
    question.answers = answers;

    return question;
  }

  private static SCQuestion readSCQuestion(int id, String title, String text, List<ChoiceAnswer> answers) {
    SCQuestion question = SCQuestion.finder.byId(id);

    if(question == null)
      question = new SCQuestion(id);

    question.title = title;
    question.text = text;
    question.answers = answers;

    return question;
  }

  private ChoiceAnswer readAnswer(JsonNode answerNode) {
    JsonNode idNode = answerNode.get(ID_NAME);
    JsonNode correctnessNode = answerNode.get("correctness");
    JsonNode textNode = answerNode.get(TEXT_NAME);

    int id = idNode.asInt();
    Correctness correctness = Correctness.valueOf(correctnessNode.asText());
    String text = textNode.asText();

    return new ChoiceAnswer(id, correctness, text);
  }

  private List<ChoiceAnswer> readAnswers(JsonNode answersNode) {
    List<ChoiceAnswer> answers = new ArrayList<>(answersNode.size());
    for(JsonNode answerNode: answersNode)
      answers.add(readAnswer(answerNode));
    return answers;
  }

  @Override
  protected ChoiceQuestion readExercise(JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get(ID_NAME);
    JsonNode typeNode = exerciseNode.get("type");
    JsonNode titleNode = exerciseNode.get(TITLE_NAME);
    JsonNode questionNode = exerciseNode.get(TEXT_NAME);
    JsonNode answersNode = exerciseNode.get("answers");
    
    String type = typeNode.asText();
    
    int id = idNode.asInt();
    String title = titleNode.asText();
    String question = questionNode.asText();
    List<ChoiceAnswer> answers = readAnswers(answersNode);

    if("MC".equals(type))
      return readMCQuestion(id, title, question, answers);
    else if("SC".equals(type))
      return readSCQuestion(id, title, question, answers);
    else
      return null;
  }

}
