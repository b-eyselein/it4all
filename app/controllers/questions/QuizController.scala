package controllers.questions

import javax.inject._

import controllers.core.excontrollers.ExerciseCollectionController
import model.User
import model.core._
import model.core.result._
import model.questions._
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.util.Try

class QuizController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)
                              (implicit ec: ExecutionContext)
  extends ExerciseCollectionController[Question, Quiz, EvaluationResult](cc, dbcp, r, "question") with Secured {

  override type SolType = StringSolution

  override def solForm: Form[StringSolution] = ???

  override def correctPart(sol: StringSolution, question: Option[Question], part: String, user: User): Try[CompleteResult[EvaluationResult]]
  = ??? // FIXME: implement...

  def quiz(id: Int): EssentialAction = withUser { user => implicit request => Ok(views.html.questions.quiz.render(user, null /* Quiz.finder.byId(id)*/)) }

  def quizCorrection(quizId: Int, questionId: Int): EssentialAction = withUser { user =>
    implicit request =>
      // User user = BaseController.user
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

  def quizQuestion(quizId: Int, questionId: Int): EssentialAction = withUser { user =>
    implicit request =>
      Ok(views.html.questions.quizQuestion.render(user, null /* Quiz.finder.byId(quizId)*/ , questionId - 1))
  }

  def quizStart(quizId: Int): EssentialAction = withUser { user => implicit request => Redirect(controllers.questions.routes.QuizController.quizQuestion(quizId, 1)) }

  def quizzes: EssentialAction = withUser { user => implicit request => Ok(views.html.questions.quizzes.render(user, null /*Quiz.finder.all.asScala.toList*/)) }

  override def renderResult(correctionResult: CompleteResult[EvaluationResult]): Html = ??? //FIXME: implement...


}
