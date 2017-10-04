package controllers.questions

import controllers.core.ExerciseCollectionController
import javax.inject.Inject
import model.question.Question
import model.quiz.Quiz
import model.user.User
import model.result.CompleteResult
import play.data.{DynamicForm, FormFactory}
import controllers.core.BaseController
import play.mvc.Results
import play.mvc.Controller

class QuizController @Inject() (f: FormFactory)
  extends ExerciseCollectionController[Question, Quiz](f, "question", Quiz.finder, Question.finder) {

  override def correctPart(form: DynamicForm, question: Question, part: String, user: User) = // FIXME: implement...
    null

  def quiz(id: Int) = Results.ok(views.html.quiz.render(BaseController.getUser, Quiz.finder.byId(id)))

  def quizCorrection(quizId: Int, questionId: Int) = {
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

  def quizQuestion(quizId: Int, questionId: Int) =
    Results.ok(views.html.quizQuestion.render(BaseController.getUser, Quiz.finder.byId(quizId), questionId - 1))

  def quizStart(quizId: Int) = Results.redirect(controllers.questions.routes.QuizController.quizQuestion(quizId, 1))

  def quizzes = Results.ok(views.html.quizzes.render(BaseController.getUser, Quiz.finder.all()))

  override def renderResult(correctionResult: CompleteResult[_]) = //FIXME: implement...
    null

}
