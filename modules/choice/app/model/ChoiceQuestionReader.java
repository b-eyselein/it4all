package model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.Correctness;
import model.QuestionType;
import model.exercisereading.ExerciseReader;

public class ChoiceQuestionReader extends ExerciseReader<ChoiceQuiz> {

  public ChoiceQuestionReader() {
    super("choice");
  }

  private ChoiceAnswer readAnswer(JsonNode answerNode, int quizId, int questionId) {
    JsonNode idNode = answerNode.get(ID_NAME);
    JsonNode correctnessNode = answerNode.get("correctness");
    JsonNode textNode = answerNode.get(TEXT_NAME);

    ChoiceAnswerKey key = new ChoiceAnswerKey(quizId, questionId, idNode.asInt());

    ChoiceAnswer answer = ChoiceAnswer.finder.byId(key);
    if(answer == null)
      answer = new ChoiceAnswer(key);

    answer.correctness = Correctness.valueOf(correctnessNode.asText());
    answer.text = textNode.asText();

    return answer;
  }

  private List<ChoiceAnswer> readAnswers(JsonNode answersNode, int quizId, int questionId) {
    List<ChoiceAnswer> answers = new ArrayList<>(answersNode.size());

    for(JsonNode answerNode: answersNode)
      answers.add(readAnswer(answerNode, quizId, questionId));

    return answers;
  }

  private ChoiceQuestion readQuestion(JsonNode questionNode, int quizId) {
    JsonNode idNode = questionNode.get(ID_NAME);
    JsonNode textNode = questionNode.get(TEXT_NAME);
    JsonNode typeNode = questionNode.get("type");
    JsonNode answersNode = questionNode.get("answers");

    int id = idNode.asInt();
    String text = textNode.asText();
    QuestionType type = QuestionType.valueOf(typeNode.asText());
    List<ChoiceAnswer> answers = readAnswers(answersNode, quizId, id);

    ChoiceQuestionKey key = new ChoiceQuestionKey(quizId, id);

    ChoiceQuestion question = ChoiceQuestion.finder.byId(key);

    if(question == null)
      question = new ChoiceQuestion(key);

    question.text = text;
    question.questionType = type;
    question.answers = answers;

    return question;

  }

  private List<ChoiceQuestion> readQuestions(JsonNode questionsNode, int quizId) {
    List<ChoiceQuestion> questions = new ArrayList<>(questionsNode.size());

    for(JsonNode questionNode: questionsNode)
      questions.add(readQuestion(questionNode, quizId));

    return questions;
  }

  @Override
  protected ChoiceQuiz readExercise(JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get(ID_NAME);
    JsonNode titleNode = exerciseNode.get(TITLE_NAME);
    JsonNode textNode = exerciseNode.get(TEXT_NAME);
    JsonNode questionsNode = exerciseNode.get("questions");

    int id = idNode.asInt();
    String title = titleNode.asText();
    String text = textNode.asText();
    List<ChoiceQuestion> questions = readQuestions(questionsNode, id);

    ChoiceQuiz quiz = ChoiceQuiz.finder.byId(id);
    if(quiz == null)
      quiz = new ChoiceQuiz(id);

    quiz.title = title;
    quiz.text = text;
    quiz.questions = questions;

    return quiz;

  }

}
