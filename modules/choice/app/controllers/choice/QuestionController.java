package controllers.choice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.Answer;
import model.AnswerKey;
import model.Question;
import model.QuestionRating;
import model.QuestionRatingKey;
import model.Quiz;
import model.QuestionResult;
import model.Correctness;
import model.QuestionType;
import model.QuestionUser;
import model.Util;
import model.user.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

public class QuestionController extends ExerciseController {
  
  @Inject
  public QuestionController(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }
  
  public Result allQuestions() {
    return ok(views.html.questions.render(getUser(), Question.finder.all()));
  }
  
  public Result correct(int id) {
    User user = getUser();
    
    QuestionUser questionUser = QuestionUser.finder.byId(user.name);
    if(questionUser == null)
      questionUser = new QuestionUser(user.name);
    questionUser.save();
    
    Question question = Question.finder.byId(id);
    DynamicForm form = factory.form().bindFromRequest();
    
    List<Answer> selectedAnswers = readSelAnswers(question, form);
    QuestionResult result = new QuestionResult(selectedAnswers, question);
    
    int stars = Integer.parseInt(form.get("stars"));
    if(stars != -1)
      saveRating(user.name, question.id, stars);
    
    switch(question.questionType) {
    case MULTIPLE:
      return ok(views.html.mcresult.render(getUser(), result));
    case SINGLE:
      return ok(views.html.scresult.render(getUser(), result));
    case FILLOUT_WITH_ORDER:
    case FILLOUT_WITHOUT_ORDER:
      return ok(views.html.filloutresult.render(getUser(), result));
    default:
      return redirect(routes.QuestionController.index());
    }
  }
  
  public Result index() {
    return ok(views.html.choiceoverview.render(getUser(), Quiz.finder.all()));
  }
  
  public Result newQuestion() {
    DynamicForm form = factory.form().bindFromRequest();
    
    int id = findMinimalNotUsedId();
    
    Question question = new Question(id);
    question.title = form.get("title");
    question.text = form.get("text");
    question.author = getUser().name;
    question.questionType = QuestionType.valueOf(form.get("type"));
    
    // FIXME: Answers...
    int numOfAnswers = Integer.parseInt(form.get("numOfAnswers"));
    question.answers = new ArrayList<>(numOfAnswers);
    
    for(int count = 1; count <= numOfAnswers; count++) {
      AnswerKey key = new AnswerKey(id, count);
      Answer answer = Answer.finder.byId(key);
      if(answer == null)
        answer = new Answer(key);
      // TODO: Korrektheit der Antwort bei MC und SC!
      answer.correctness = Correctness.WRONG; // Correctness.valueOf("");
      answer.text = form.get(Integer.toString(count));
      
      question.answers.add(answer);
    }
    
    question.save();
    for(Answer answer: question.answers)
      answer.save();
    
    return ok(views.html.choicecreation.render(getUser(), Arrays.asList(question)));
  }
  
  public Result newQuestionForm() {
    return ok(views.html.newquestionform.render(getUser()));
  }
  
  public Result notAssignedQuestions() {
    List<Question> notAssignedQuestions = Question.finder.all().stream().filter(q -> q.quizzes.isEmpty())
        .collect(Collectors.toList());
    
    return ok(views.html.notassignedquestions.render(getUser(), notAssignedQuestions));
  }
  
  public Result question(int id) {
    Question question = Question.finder.byId(id);
    
    if(question == null)
      return redirect(controllers.choice.routes.QuestionController.index());
    
    switch(question.questionType) {
    case MULTIPLE:
      return ok(views.html.mcquestion.render(getUser(), question));
    case SINGLE:
      return ok(views.html.scquestion.render(getUser(), question));
    case FILLOUT_WITH_ORDER:
    case FILLOUT_WITHOUT_ORDER:
      return ok(views.html.filloutquestion.render(getUser(), question));
    default:
      return redirect(routes.QuestionController.index());
    }
  }
  
  public Result quiz(int id) {
    return ok(views.html.quiz.render(getUser(), Quiz.finder.byId(id)));
  }
  
  private int findMinimalNotUsedId() {
    // FIXME: this is probably a ugly hack...
    List<Question> questions = Question.finder.order().asc("id").findList();
    
    if(questions.isEmpty())
      return 1;
    
    for(int i = 0; i < questions.size() - 1; i++)
      if(questions.get(i).id < questions.get(i + 1).id - 1)
        return questions.get(i).id + 1;
      
    return questions.get(questions.size() - 1).id + 1;
  }
  
  private List<Answer> readSelAnswers(Question question, DynamicForm form) {
    switch(question.questionType) {
    case MULTIPLE:
    // @formatter:off
    return question.answers.stream()
        .filter(ans -> form.get(Integer.toString(ans.getId())) != null)
        .collect(Collectors.toList());
    //@formatter:on
    case SINGLE:
      return new ArrayList<>(Arrays.asList(question.getAnswer(Integer.parseInt(form.get("ans")))));
    case FILLOUT_WITH_ORDER:
    default:
      return Collections.emptyList();
    }
  }
  
  private void saveRating(String name, int id, int stars) {
    QuestionRatingKey key = new QuestionRatingKey(id, name);
    QuestionRating rating = QuestionRating.finder.byId(key);
    
    if(rating == null)
      rating = new QuestionRating(key);
    
    rating.rating = stars;
    rating.save();
  }
  
}
