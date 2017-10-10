package controllers.questions;

import controllers.core.IdExController;
import model.QuestionResult;
import model.QuestionUser;
import model.UserAnswer;
import model.UserAnswerKey;
import model.question.Answer;
import model.question.Question;
import model.question.QuestionReader;
import model.quiz.Quiz;
import model.result.CompleteResult;
import model.user.Role;
import model.user.User;
import play.api.Configuration;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Result;
import play.twirl.api.Html;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionController extends IdExController<Question, QuestionResult> {

    @Inject
    public QuestionController(Configuration c, FormFactory f) {
        super(c, f, Question.finder, new QuestionToolObject(c));
    }

    @Override
    public User getUser() {
        final User user = super.getUser();

        if (QuestionUser.finder.byId(user.name) == null)
            // Make sure there is a corresponding entrance in other db...
            new QuestionUser(user.name).save();

        return user;
    }

    // private static List<Answer> readAnswersFromForm(DynamicForm form, int
    // questionId, boolean isChoice) {
    // return IntStream.range(0, Question.MAX_ANSWERS).mapToObj(id -> {
    // final AnswerKey key = new AnswerKey(questionId, id);
    //
    // Answer answer = Answer.finder.byId(key);
    // if(answer == null)
    // answer = new Answer(key);
    //
    // answer.text = form.get(String.valueOf(id));
    // answer.correctness = isChoice ? Correctness.valueOf(form.get("correctness_"
    // + id)) : Correctness.CORRECT;
    //
    // return answer;
    // }).filter(ans -> !ans.text.isEmpty()).collect(Collectors.toList());
    // }

    private static List<Answer> readSelAnswers(Question question, DynamicForm form) {
        return question.getAnswers().stream().filter(ans -> form.get(Integer.toString(ans.key.id)) != null)
                .collect(Collectors.toList());
    }

    @Override
    public scala.util.Try<CompleteResult<QuestionResult>> correctEx(DynamicForm form, Question exercise,
                                                                    User user) {
        // TODO Auto-generated method stub
        return null;
    }

    public Result editQuestion(int id) {
        // DynamicForm form = factory.form().bindFromRequest();

        final Question question = null; // readQuestionFromForm(form,
        // Question.finder.byId(id));
        // question.save();
        // for(Answer answer: question.answers)
        // answer.save();

        return ok(views.html.questionAdmin.questionCreated.render(getUser(), Arrays.asList(question)));
    }

    public Result editQuestionForm(int id) {
        final User user = getUser();
        final Question question = Question.finder.byId(id);

        if (question.getAuthor().equals(user.name) || user.stdRole == Role.ADMIN)
            return ok(views.html.editQuestionForm.render(user, question, true));

        return redirect(routes.QuestionController.index(0));
    }

    @Override
    public Result index(int start) {
        final List<Question> all = Question.finder.all();
        final List<Question> questions = all.subList(Math.min(all.size(), start), Math.min(all.size(), start + STEP()));
        return ok(views.html.questionIndex.render(getUser(), questions, Quiz.finder.all()));
    }

    public Result newQuestion(boolean isFreetext) {
        final QuestionReader reader = QuestionReader.getInstance();

        final Question question = reader.initFromForm(0, factory().form().bindFromRequest());

        reader.save(question);

        return ok(views.html.questionAdmin.questionCreated.render(getUser(), Arrays.asList(question)));
    }

    public Result newQuestionForm(boolean isFreetext) {
        if (isFreetext)
            return ok(views.html.question.newFreetextQuestionForm.render(getUser(), Integer.MAX_VALUE));

        // TODO: Unterscheidung zwischen ausfuellen und ankreuzen!
        final boolean isChoice = true;
        return ok(views.html.question.newQuestionForm.render(getUser(), Integer.MAX_VALUE, isChoice));
    }

    public Result questionResult(int id) {
        final User user = getUser();
        final Question question = Question.finder.byId(id);

        if (question.getQuestionType() != Question.QType.FREETEXT) {
            // FILLOUT or MULTIPLE CHOICE
            final DynamicForm form = factory().form().bindFromRequest();
            final QuestionResult result = new QuestionResult(readSelAnswers(question, form), question);
            return ok(views.html.givenanswerQuestionResult.render(user, result));
        }

        final UserAnswerKey key = new UserAnswerKey(user.name, id);
        UserAnswer answer = UserAnswer.finder.byId(key);

        if (answer == null)
            answer = new UserAnswer(key);

        answer.question = Question.finder.byId(id);
        answer.text = factory().form().bindFromRequest().get("answer");
        answer.save();

        return ok(views.html.freetextQuestionResult.render(getUser(), question, answer));
    }

    @Override
    public Html renderExercise(User user, Question exercise) {
        final UserAnswer oldAnswer = UserAnswer.finder.byId(new UserAnswerKey(user.name, exercise.getId()));
        return views.html.question.question.render(user, exercise, oldAnswer);
    }

    @Override
    public Html renderExesListRest() {
        // TODO Auto-generated method stub
        return new Html("");
    }

    @Override
    public Html renderResult(CompleteResult<QuestionResult> correctionResult) {
        // TODO Auto-generated method stub
        return new Html("");
    }

}
