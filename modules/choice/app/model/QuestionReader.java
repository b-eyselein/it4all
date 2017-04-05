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
  
  private Answer readAnswer(JsonNode answerNode, int questionId) {
    JsonNode idNode = answerNode.get(ID_NAME);
    JsonNode correctnessNode = answerNode.get("correctness");
    JsonNode textNode = answerNode.get(TEXT_NAME);
    
    AnswerKey key = new AnswerKey(questionId, idNode.asInt());
    
    Answer answer = Answer.finder.byId(key);
    if(answer == null)
      answer = new Answer(key);
    
    answer.correctness = Correctness.valueOf(correctnessNode.asText());
    answer.text = textNode.asText();
    
    return answer;
  }
  
  private List<Answer> readAnswers(JsonNode answersNode, int questionId) {
    List<Answer> answers = new ArrayList<>(answersNode.size());
    
    for(JsonNode answerNode: answersNode)
      answers.add(readAnswer(answerNode, questionId));
    
    return answers;
  }
  
  @Override
  protected Question readExercise(JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get(ID_NAME);
    JsonNode titleNode = exerciseNode.get(TITLE_NAME);
    JsonNode textNode = exerciseNode.get(TEXT_NAME);
    JsonNode typeNode = exerciseNode.get("type");
    JsonNode answersNode = exerciseNode.get("answers");
    
    int id = idNode.asInt();
    
    Question question = Question.finder.byId(id);
    if(question == null)
      question = new Question(id);
    
    // TODO: name of author?
    question.author = "admin";
    question.title = titleNode.asText();
    question.text = textNode.asText();
    question.questionType = QuestionType.valueOf(typeNode.asText());
    question.answers = readAnswers(answersNode, id);
    
    return question;
  }
  
}
