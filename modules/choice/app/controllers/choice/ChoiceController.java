package controllers.choice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import controllers.core.ExerciseController;
import model.ChoiceAnswer;
import model.ChoiceAnswerKey;
import model.ChoiceQuestion;
import model.ChoiceQuiz;
import model.ChoiceResult;
import model.Correctness;
import model.QuestionType;
import model.Util;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;

public class ChoiceController extends ExerciseController {

  @Inject
  public ChoiceController(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory);
  }

  public Result correct(int id) {
    ChoiceQuestion question = ChoiceQuestion.finder.byId(id);
    DynamicForm form = factory.form().bindFromRequest();

    List<ChoiceAnswer> selectedAnswers = readSelAnswers(question, form);
    ChoiceResult result = new ChoiceResult(selectedAnswers, question);

    switch(question.questionType) {
    case MULTIPLE:
      return ok(views.html.mcresult.render(getUser(), result));
    case SINGLE:
      return ok(views.html.scresult.render(getUser(), result));
    case FILLOUT_WITH_ORDER:
      return ok(views.html.filloutresult.render(getUser(), result));
    default:
      return redirect(routes.ChoiceController.index());
    }
  }

  public Result index() {
    return ok(views.html.choiceoverview.render(getUser(), ChoiceQuiz.finder.all()));
  }

  public Result newQuestion() {
    DynamicForm form = factory.form().bindFromRequest();

    int id = findMinimalNotUsedId();

    ChoiceQuestion question = new ChoiceQuestion(id);
    question.title = form.get("title");
    question.text = form.get("text");
    question.author = getUser().name;
    question.questionType = QuestionType.valueOf(form.get("type"));

    // FIXME: Answers...
    int numOfAnswers = Integer.parseInt(form.get("numOfAnswers"));
    question.answers = new ArrayList<>(numOfAnswers);

    for(int count = 1; count <= numOfAnswers; count++) {
      ChoiceAnswerKey key = new ChoiceAnswerKey(id, count);
      ChoiceAnswer answer = ChoiceAnswer.finder.byId(key);
      if(answer == null)
        answer = new ChoiceAnswer(key);
      // TODO: Korrektheit der Antwort bei MC und SC!
      answer.correctness = Correctness.WRONG; // Correctness.valueOf("");
      answer.text = form.get(Integer.toString(count));

      question.answers.add(answer);
    }

    question.save();
    for(ChoiceAnswer answer: question.answers)
      answer.save();

    return ok(views.html.choicecreation.render(getUser(), Arrays.asList(question)));
  }

  public Result newQuestionForm() {
    return ok(views.html.newquestionform.render(getUser()));
  }

  public Result notAssignedQuestions() {
    List<ChoiceQuestion> notAssignedQuestions = ChoiceQuestion.finder.all().stream().filter(q -> q.quizzes.isEmpty())
        .collect(Collectors.toList());

    return ok(views.html.notassignedquestions.render(getUser(), notAssignedQuestions));
  }

  public Result question(int id) {
    ChoiceQuestion question = ChoiceQuestion.finder.byId(id);

    if(question == null)
      return redirect(controllers.choice.routes.ChoiceController.index());

    switch(question.questionType) {
    case MULTIPLE:
      return ok(views.html.mcquestion.render(getUser(), question));
    case SINGLE:
      return ok(views.html.scquestion.render(getUser(), question));
    case FILLOUT_WITH_ORDER:
      return ok(views.html.filloutquestion.render(getUser(), question));
    default:
      return redirect(routes.ChoiceController.index());
    }
  }

  public Result quiz(int id) {
    return ok(views.html.quiz.render(getUser(), ChoiceQuiz.finder.byId(id)));
  }

  private int findMinimalNotUsedId() {
    // FIXME: this is probably a ugly hack...
    List<ChoiceQuestion> questions = ChoiceQuestion.finder.order().asc("id").findList();

    if(questions.isEmpty())
      return 1;

    for(int i = 0; i < questions.size() - 1; i++)
      if(questions.get(i).id < questions.get(i + 1).id - 1)
        return questions.get(i).id + 1;

    return questions.get(questions.size() - 1).id + 1;
  }

  private List<ChoiceAnswer> readSelAnswers(ChoiceQuestion question, DynamicForm form) {
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

}
