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
  
  private ChoiceAnswer readAnswer(JsonNode answerNode, int questionId) {
    JsonNode idNode = answerNode.get(ID_NAME);
    JsonNode correctnessNode = answerNode.get("correctness");
    JsonNode textNode = answerNode.get(TEXT_NAME);
    
    ChoiceAnswerKey key = new ChoiceAnswerKey(questionId, idNode.asInt());
    
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
    JsonNode titleNode = exerciseNode.get(TITLE_NAME);
    JsonNode textNode = exerciseNode.get(TEXT_NAME);
    JsonNode typeNode = exerciseNode.get("type");
    JsonNode answersNode = exerciseNode.get("answers");
    
    int id = idNode.asInt();
    
    ChoiceQuestion question = ChoiceQuestion.finder.byId(id);
    if(question == null)
      question = new ChoiceQuestion(id);
    
    // TODO: name of author?
    question.author = "admin";
    question.title = titleNode.asText();
    question.text = textNode.asText();
    question.questionType = QuestionType.valueOf(typeNode.asText());
    question.answers = readAnswers(answersNode, id);
    
    return question;
  }
  
}
