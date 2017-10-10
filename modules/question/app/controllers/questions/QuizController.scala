package controllers.questions

import javax.inject.Inject

import controllers.core.ExerciseCollectionController
import model.question.Question
import model.quiz.Quiz
import model.result.CompleteResult
import model.user.User
import play.api.Configuration
import play.data.{DynamicForm, FormFactory}
import play.mvc.{Result, Results}
import play.twirl.api.Html

import scala.util.Try

class QuizController @Inject()(c: Configuration, f: FormFactory)
  extends ExerciseCollectionController[Question, Quiz](c, f, "question", Quiz.finder, Question.finder) {

  override def correctPart(form: DynamicForm, question: Question, part: String, user: User): Try[model.result.CompleteResult[_]] = ??? // FIXME: implement...

  def quiz(id: Int): Result = Results.ok(views.html.quiz.render(getUser, Quiz.finder.byId(id)))

  def quizCorrection(quizId: Int, questionId: Int): Result = {
    // User user = BaseController.getUser
    //
    // Quiz quiz = Quiz.finder.byId(quizId)
    //
    // Question question = quiz.questions.get(questionId - 1)
    // DynamicForm form = factory.form().bindFromRequest()
    //
    // List<Answer> selectedAnswers = readSelAnswers(question, form)
    // QuestionResult result = new QuestionResult(selectedAnswers, question)
    //
    //   return ok(views.html.quizQuestionResult.render(user, quiz, result))
    Results.ok("TODO!")
  }

  def quizQuestion(quizId: Int, questionId: Int): Result =
    Results.ok(views.html.quizQuestion.render(getUser, Quiz.finder.byId(quizId), questionId - 1))

  def quizStart(quizId: Int): Result = Results.redirect(controllers.questions.routes.QuizController.quizQuestion(quizId, 1))

  def quizzes: Result = Results.ok(views.html.quizzes.render(getUser, Quiz.finder.all))

  override def renderResult(correctionResult: CompleteResult[_]): Html = ??? //FIXME: implement...


}
