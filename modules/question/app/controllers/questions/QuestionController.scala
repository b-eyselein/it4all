package controllers.questions

import javax.inject.Inject

import controllers.excontrollers.IdExController
import controllers.excontrollers.IdExController._
import model._
import model.question.Question
import model.quiz.Quiz
import model.result.CompleteResult
import model.user.{Role, User}
import play.api.data.Form
import play.api.mvc.{AnyContent, ControllerComponents, Request, Session}
import play.twirl.api.Html

import scala.collection.JavaConverters._
import scala.util.Try

class QuestionController @Inject()(cc: ControllerComponents) extends IdExController[Question, QuestionResult](cc, Question.finder, QuestionToolObject) {

  override type SolType = StringSolution

  override def solForm: Form[StringSolution] = ???

  override def getUser(implicit request: Request[AnyContent]): User = {
    val user = super.getUser

    Option(QuestionUser.finder.byId(user.name)) match {
      case None => new QuestionUser(user.name).save()
      case Some(_) => Unit
    }

    user
  }

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


  override def correctEx(sol: StringSolution, exercise: Question, user: User): Try[CompleteResult[QuestionResult]] = ???

  def editQuestion(id: Int) = Action { implicit request =>
    // DynamicForm form = factory.form().bindFromRequest()

    val question: Question = null // readQuestionFromForm(form,
    // Question.finder.byId(id))
    // question.save()
    // for(Answer answer: question.answers)
    // answer.save()

    Ok(views.html.questionAdmin.questionCreated.render(getUser, List(question)))
  }

  def editQuestionForm(id: Int) = Action { implicit request =>
    val user = getUser
    val question = Question.finder.byId(id)

    if (question.author.equals(user.name) || user.stdRole == Role.ADMIN)
      Ok(views.html.editQuestionForm.render(user, question, isEdit = true))
    else
      Redirect(routes.QuestionController.index(0))
  }

  override def index(start: Int) = Action { implicit request =>
    val all = Question.finder.all
    val questions = all.subList(Math.min(all.size, start), Math.min(all.size, start + STEP))
    Ok(views.html.questionIndex.render(getUser, questions.asScala.toList, Quiz.finder.all.asScala.toList))
  }

  def newQuestion(isFreetext: Boolean) = Action { implicit request =>
    //    QuestionReader.initFromForm(0, null /* factory.form().bindFromRequest()*/) match {
    //      case ReadingError(_, _, _) => BadRequest("There has been an error...")
    //      case ReadingFailure(_) => BadRequest("There has been an error")
    //      case ReadingResult(questions) =>
    //        val question = questions.head.read.asInstanceOf[Question]
    //        QuestionReader.save(question)
    //        Ok(views.html.questionAdmin.questionCreated.render(getUser, List(question)))
    //    }
    Ok("TODO!")
  }

  def newQuestionForm(isFreetext: Boolean) = Action { implicit request =>
    if (isFreetext)
      Ok(views.html.newFreetextQuestionForm.render(getUser, Integer.MAX_VALUE))
    else {
      // TODO: Unterscheidung zwischen ausfuellen und ankreuzen!
      Ok(views.html.newQuestionForm.render(getUser, Integer.MAX_VALUE, isChoice = true))
    }
  }


  def questionResult(id: Int) = Action { implicit request =>
    val user = getUser
    val question = Question.finder.byId(id)

    if (question.questionType != Question.QType.FREETEXT) {
      // FILLOUT or MULTIPLE CHOICE
      //      val form = factory.form().bindFromRequest()
      //      val result = new QuestionResult(readSelAnswers(question, form), question)
      Ok(views.html.givenanswerQuestionResult.render(user, null /* result*/))
    } else {
      val key = new UserAnswerKey(user.name, id)
      val answer = Option(UserAnswer.finder.byId(key)).getOrElse(new UserAnswer(key))


      answer.question = Question.finder.byId(id)
      //    answer.text = factory.form().bindFromRequest().get("answer")
      answer.save()

      Ok(views.html.freetextQuestionResult.render(getUser, question, answer))
    }
  }

  override def renderExercise(user: User, exercise: Question): Html = {
    views.html.question.render(user, exercise, UserAnswer.finder.byId(new UserAnswerKey(user.name, exercise.id)))
  }

  override def renderExesListRest: Html = ???

  override def renderResult(correctionResult: CompleteResult[QuestionResult]): Html = ???

}
