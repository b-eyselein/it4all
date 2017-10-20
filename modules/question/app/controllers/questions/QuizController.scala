package controllers.questions

import javax.inject.Inject

import controllers.excontrollers.ExerciseCollectionController
import model.StringSolution
import model.question.Question
import model.quiz.Quiz
import model.result.CompleteResult
import model.user.User
import play.api.data.Form
import play.api.mvc.ControllerComponents
import play.twirl.api.Html

import scala.collection.JavaConverters._
import scala.util.Try

class QuizController @Inject()(cc: ControllerComponents)
  extends ExerciseCollectionController[Question, Quiz](cc, "question", Quiz.finder, Question.finder) {

  override type SolType = StringSolution

  override def solForm: Form[StringSolution] = ???

  override def correctPart(sol: StringSolution, question: Question, part: String, user: User): Try[model.result.CompleteResult[_]]
  = ??? // FIXME: implement...

  def quiz(id: Int) = Action { implicit request => Ok(views.html.quiz.render(getUser, Quiz.finder.byId(id))) }

  def quizCorrection(quizId: Int, questionId: Int) = Action { implicit request =>
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
    Ok("TODO!")
  }

  def quizQuestion(quizId: Int, questionId: Int) = Action { implicit request =>
    Ok(views.html.quizQuestion.render(getUser, Quiz.finder.byId(quizId), questionId - 1))
  }

  def quizStart(quizId: Int) = Action { implicit request => Redirect(controllers.questions.routes.QuizController.quizQuestion(quizId, 1)) }

  def quizzes = Action { implicit request => Ok(views.html.quizzes.render(getUser, Quiz.finder.all.asScala.toList)) }

  override def renderResult(correctionResult: CompleteResult[_]): Html = ??? //FIXME: implement...


}
