package controllers.questions

import javax.inject.Inject

import controllers.core.IdExController
import model.exercisereading.{ReadingError, ReadingFailure, ReadingResult}
import model.{QuestionResult, QuestionUser, UserAnswer, UserAnswerKey}
import model.question.{Answer, Question, QuestionReader}
import model.quiz.Quiz
import model.result.CompleteResult
import model.user.{Role, User}
import play.data.{DynamicForm, FormFactory}
import play.mvc.Result
import play.mvc.Results._
import play.twirl.api.Html

import scala.collection.JavaConverters._
import scala.util.Try

class QuestionController @Inject()(f: FormFactory) extends IdExController[Question, QuestionResult](f, Question.finder, QuestionToolObject) {

  override def getUser: User = {
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

  private def readSelAnswers(question: Question, form: DynamicForm): List[Answer] =
    question.answers.asScala.filter(ans => Option(form.get(ans.key.id.toString)).isDefined).toList


  override def correctEx(form: DynamicForm, exercise: Question, user: User): Try[CompleteResult[QuestionResult]] = ???

  def editQuestion(id: Int): Result = {
    // DynamicForm form = factory.form().bindFromRequest()

    val question: Question = null // readQuestionFromForm(form,
    // Question.finder.byId(id))
    // question.save()
    // for(Answer answer: question.answers)
    // answer.save()

    ok(views.html.questionAdmin.questionCreated.render(getUser, List(question).asJava))
  }

  def editQuestionForm(id: Int): Result = {
    val user = getUser
    val question = Question.finder.byId(id)

    if (question.author.equals(user.name) || user.stdRole == Role.ADMIN)
      return ok(views.html.editQuestionForm.render(user, question, true))

    redirect(routes.QuestionController.index(0))
  }

  override def index(start: Int): Result = {
    val all = Question.finder.all
    val questions = all.subList(Math.min(all.size, start), Math.min(all.size, start + STEP))
    ok(views.html.questionIndex.render(getUser, questions, Quiz.finder.all))
  }

  def newQuestion(isFreetext: Boolean): Result = QuestionReader.initFromForm(0, factory.form().bindFromRequest()) match {
    case ReadingError(_, _, _) => badRequest("There has been an error...")
    case ReadingFailure(_) => badRequest("There has been an error")
    case ReadingResult(questions) =>
      val question = questions.head.read.asInstanceOf[Question]
      QuestionReader.save(question)
      ok(views.html.questionAdmin.questionCreated.render(getUser, List(question).asJava))
  }

  def newQuestionForm(isFreetext: Boolean): Result = if (isFreetext)
    ok(views.html.newFreetextQuestionForm.render(getUser, Integer.MAX_VALUE))
  else {
    // TODO: Unterscheidung zwischen ausfuellen und ankreuzen!
    ok(views.html.newQuestionForm.render(getUser, Integer.MAX_VALUE, isChoice = true))
  }


  def questionResult(id: Int): Result = {
    val user = getUser
    val question = Question.finder.byId(id)

    if (question.questionType != Question.QType.FREETEXT) {
      // FILLOUT or MULTIPLE CHOICE
      val form = factory.form().bindFromRequest()
      val result = new QuestionResult(readSelAnswers(question, form), question)
      return ok(views.html.givenanswerQuestionResult.render(user, result))
    }

    val key = new UserAnswerKey(user.name, id)
    val answer = Option(UserAnswer.finder.byId(key)).getOrElse(new UserAnswer(key))


    answer.question = Question.finder.byId(id)
    answer.text = factory.form().bindFromRequest().get("answer")
    answer.save()

    ok(views.html.freetextQuestionResult.render(getUser, question, answer))
  }

  override def renderExercise(user: User, exercise: Question): Html = {
    views.html.question.render(user, exercise, UserAnswer.finder.byId(new UserAnswerKey(user.name, exercise.id)))
  }

  override def renderExesListRest: Html = ???

  override def renderResult(correctionResult: CompleteResult[QuestionResult]): Html = ???

}
