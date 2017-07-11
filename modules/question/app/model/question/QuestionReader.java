package model.question;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

import model.JsonWrapper;
import model.StringConsts;
import model.exercisereading.ExerciseReader;
import play.libs.Json;

public class QuestionReader extends ExerciseReader<Question> {
  
  public QuestionReader() {
    super("question");
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
    
    Question question = Question.finder.byId(id);
    if(question == null)
      question = new Question(id);
    
    question.title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    question.author = exerciseNode.get(StringConsts.AUTHOR_NAME).asText();
    question.text = JsonWrapper.readTextArray(exerciseNode.get(StringConsts.TEXT_NAME), "");
    question.maxPoints = exerciseNode.get("maxPoints").asInt();
    question.questionType = Question.QType.valueOf(exerciseNode.get("questionType").asText());
    question.answers = readAnswers(exerciseNode.get("answers"));
    
    return question;
  }
  
}
