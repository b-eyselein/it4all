package controllers.questions

import javax.inject._

import controllers.core.excontrollers.IdExController
import model.Enums.Role
import model.User
import model.core._
import model.core.result.CompleteResult
import model.questions._
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.util.Try

class QuestionController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)
                                  (implicit ec: ExecutionContext)
  extends IdExController[Question, QuestionResult](cc, dbcp, r, QuestionToolObject) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  override type SolType = StringSolution

  override def solForm: Form[StringSolution] = ???

  //  override def user(implicit request: Request[AnyContent]): User = {
  //    val user = super.user
  //
  //    Option(QuestionUser.finder.byId(user.name)) match {
  //      case None => new QuestionUser(user.name).save()
  //      case Some(_) => Unit
  //    }
  //
  //    user
  //  }

  // private static List<Answer> readAnswersFromForm(DynamicForm form, int
  // questionId, boolean isChoice) {
  // return IntStream.range(0, Question.MAX_ANSWERS).mapToObj(id -> {
  // final AnswerKey key = new AnswerKey(questionId, id)
  //
  // Answer answer = Answer.finder.byId(key)
  // if(answer == null)
  // answer = new Answer(key)
  //
  // answer.text = form.get(String.valueOf(id))
  // answer.correctness = isChoice ? Correctness.valueOf(form.get("correctness_"
  // + id)) : Correctness.CORRECT
  //
  // return answer
  // }).filter(ans -> !ans.text.isEmpty()).collect(Collectors.toList())
  // }

  //  private def readSelAnswers(question: Question, form: DynamicForm): List[Answer] =
  //    question.answers.asScala.filter(ans => Option(form.get(ans.key.id.toString)).isDefined).toList


  override def correctEx(sol: StringSolution, exercise: Option[Question], user: User): Try[CompleteResult[QuestionResult]] = ???

  def editQuestion(id: Int): EssentialAction = withUser { user =>
    implicit request =>
      // DynamicForm form = factory.form().bindFromRequest()

      val question: Question = null // readQuestionFromForm(form,
      // Question.finder.byId(id))
      // question.save()
      // for(Answer answer: question.answers)
      // answer.save()

      Ok(views.html.questions.questionAdmin.questionCreated.render(user, List(question)))
  }

  def editQuestionForm(id: Int): EssentialAction = withUser { user =>
    implicit request =>
      val question: Question = null /* Question.finder.byId(id)*/

      if (question.author == user.username || user.stdRole == Role.RoleAdmin)
        Ok(views.html.questions.editQuestionForm.render(user, question, isEdit = true))
      else
        Redirect(routes.QuestionController.index(0))
  }

  override def index(start: Int): EssentialAction = withUser { user =>
    implicit request =>
      //      val all = Question.finder.all
      //      val questions = all.subList(Math.min(all.size, start), Math.min(all.size, start + STEP))
      //      Ok(views.html.questionIndex.render(user, questions.asScala.toList, Quiz.finder.all.asScala.toList))
      Ok("TODO!")
  }

  def newQuestion(isFreetext: Boolean): EssentialAction = withUser { user =>
    implicit request =>
      //    QuestionReader.initFromForm(0, null /* factory.form().bindFromRequest()*/) match {
      //      case ReadingError(_, _, _) => BadRequest("There has been an error...")
      //      case ReadingFailure(_) => BadRequest("There has been an error")
      //      case ReadingResult(questions) =>
      //        val question = questions.head.read.asInstanceOf[Question]
      //        QuestionReader.save(question)
      //        Ok(views.html.questionAdmin.questionCreated.render(user, List(question)))
      //    }
      Ok("TODO!")
  }

  def newQuestionForm(isFreetext: Boolean): EssentialAction = withUser { user =>
    implicit request =>
      if (isFreetext)
        Ok(views.html.questions.newFreetextQuestionForm.render(user, Integer.MAX_VALUE))
      else {
        // TODO: Unterscheidung zwischen ausfuellen und ankreuzen!
        Ok(views.html.questions.newQuestionForm.render(user, Integer.MAX_VALUE, isChoice = true))
      }
  }


  def questionResult(id: Int): EssentialAction = withUser { user =>
    implicit request =>
      //      val question = Question.finder.byId(id)
      //
      //      if (question.questionType != Question.QType.FREETEXT) {
      //        // FILLOUT or MULTIPLE CHOICE
      //        //      val form = factory.form().bindFromRequest()
      //        //      val result = new QuestionResult(readSelAnswers(question, form), question)
      //        Ok(views.html.givenanswerQuestionResult.render(user, null /* result*/))
      //      } else {
      //        val key = new UserAnswerKey(user.name, id)
      //        val answer = Option(UserAnswer.finder.byId(key)).getOrElse(new UserAnswer(key))
      //
      //
      //        answer.question = Question.finder.byId(id)
      //        //    answer.text = factory.form().bindFromRequest().get("answer")
      //        answer.save()
      //
      //        Ok(views.html.freetextQuestionResult.render(user, question, answer))
      //  }
      Ok("TODO!")
  }

  override def renderExercise(user: User, exercise: Question): Html = {
    views.html.questions.question.render(user, exercise, null /* UserAnswer.finder.byId(new UserAnswerKey(user.name, exercise.id))*/)
  }

  override def renderExesListRest: Html = ???

  override def renderResult(correctionResult: CompleteResult[QuestionResult]): Html = ???

}
