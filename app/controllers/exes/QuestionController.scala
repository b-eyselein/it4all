package controllers.exes

import javax.inject._

import controllers.core.AExCollectionController
import model.Enums.Role
import model.User
import model.core._
import model.core.result.CompleteResult
import model.questions._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions
import scala.util.Try

class QuestionController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)
                                  (implicit ec: ExecutionContext)
  extends AExCollectionController[Question, Quiz, QuestionResult](cc, dbcp, r, QuestionToolObject) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  override type SolutionType = StringSolution

  override def solForm: Form[StringSolution] = ???

  // db

  override type DbType = Quiz

  override implicit val yamlFormat: YamlFormat[Quiz] = null

  override type TQ = repo.QuizzesTable

  override def tq = repo.quizzes

  // Quizzes


  //  override type SolType = StringSolution

  //  override def solForm: Form[StringSolution] = ???

  //  override def correctPart(sol: StringSolution, question: Option[Question], part: String, user: User): Try[CompleteResult[EvaluationResult]]
  //  = ??? // FIXME: implement...

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


  // FIXME: stubs...
  def correctPart(form: StringSolution, exercise: Option[Question], part: String, user: User): Try[CompleteResult[QuestionResult]] = ???

  def renderCollectionCreated(collections: List[model.core.SingleReadingResult[model.questions.Quiz]]): play.twirl.api.Html = ???

  def renderExCollCreationForm(user: model.User, collection: model.questions.Quiz): play.twirl.api.Html = ???

  def renderExEditForm(user: model.User, exercise: model.questions.Quiz, isCreation: Boolean): play.twirl.api.Html = ???

  def renderExerciseCollections(user: model.User, allCollections: List[model.questions.Quiz]): play.twirl.api.Html = ???

  // FIXME: stubs...

  def quizQuestion(quizId: Int, questionId: Int): EssentialAction = withUser { user =>
    implicit request =>
      Ok(views.html.questions.quizQuestion.render(user, null /* Quiz.finder.byId(quizId)*/ , questionId - 1))
  }

  def quizStart(quizId: Int): EssentialAction = withUser { user =>
    implicit request =>
      Redirect(controllers.exes.routes.QuestionController.quizQuestion(quizId, 1))
  }

  def quizzes: EssentialAction = withUser { user => implicit request => Ok(views.html.questions.quizzes.render(user, null /*Quiz.finder.all.asScala.toList*/)) }

  //  override def renderResult(correctionResult: CompleteResult[EvaluationResult]): Html = ??? //FIXME: implement...

  // Admin

  //  override protected def statistics: Future[Html] = Future(new Html(
  //    s""" in allen Kategorien
  //       |<ul>
  //       |@if(!notAssignedQuestions.isEmpty) {
  //       |<li>Es gibt noch @notAssignedQuestions.size
  //       |<a href="@questions.routes.QuestionAdmin.notAssignedQuestions">nicht zugeordnete Fragen</a>.
  //       |Sie k&ouml;nnen Sie <a href="@questions.routes.QuestionAdmin.assignQuestionsForm">hier</a> zuweisen.
  //       |</li>
  //       |}
  //       |</ul>
  //       |</li>
  //       |<li>Es existieren @model.Quiz.finder.all.size <a href="@questions.routes.QuestionAdmin.exerciseCollections">Quiz(ze)</a>""".stripMargin)

  def assignQuestion(keyAndValue: String, addOrRemove: Boolean) {
    // String[] quizAndQuestion = keyAndValue.split("_")
    //
    // Quiz quiz = Quiz.finder.byId(Integer.parseInt(quizAndQuestion[0]))
    // Question question =
    // Question.finder.byId(Integer.parseInt(quizAndQuestion[1]))
    //
    // if(addOrRemove)
    // quiz.questions.add(question)
    // else
    // quiz.questions.remove(question)
    //
    // quiz.save()
  }

  def assignQuestions: EssentialAction = withAdmin { user =>
    implicit request =>
      //    val form = factory.form().bindFromRequest()

      // Read it...
      //    Map<String, String> assignments = form.rawData()
      //    for(Map.Entry<String, String> entry: assignments.entrySet())
      //      assignQuestion(entry.getKey(), "on".equals(entry.getValue()))
      //
      //    return ok(views.html.questionAdmin.questionsAssigned.render(user(), assignments.toString()))
      Ok("TODO!")
  }

  //  override def renderCollectionCreated(collections: List[SingleReadingResult[Quiz]]): Html = ???
  //
  //  override def renderExCollCreationForm(user: User, collection: Quiz): Html = ???
  //
  //  override def renderExEditForm(user: User, exercise: Quiz, isCreation: Boolean): Html = ???
  //
  //  override def renderExerciseCollections(user: User, allCollections: List[Quiz]): Html = ???

  def assignQuestionsForm: EssentialAction = withAdmin { user =>
    implicit request =>
      Ok(views.html.questions.questionAdmin.assignQuestionsForm.render(user, /* Question.finder.all() */ List.empty, List.empty /*Quiz.finder.all.asScala.toList*/))
  }

  def assignQuestionsSingleForm(id: Int): EssentialAction = withAdmin { user =>
    implicit request =>
      Ok(views.html.questions.questionAdmin.assignQuestionsForm.render(user, /* Question.finder.all() */ List.empty, List.empty /*(Quiz.finder.byId(id))*/))
  }

  //  def  exportQuizzes = {
  //    val json = Json.prettyPrint(Json.toJson(Quiz.finder.all()))
  //
  //    try {
  //      File tempFile = new File("quizzes_export_" + LocalDateTime.now() + ".json")
  //      Files.asCharSink(tempFile, Charset.defaultCharset()).write(json)
  //      // false == download file!
  //      Ok(tempFile, false)
  //    } catch (IOException e) {
  //      Ok(json)
  //    }
  //  }

  def gradeFreetextAnswer(id: Int, user: String): EssentialAction = withAdmin { user =>
    implicit request =>
      // FreetextAnswer answer = FreetextAnswer.finder.byId(new
      // FreetextAnswerKey(user, id))
      //             views.html.questionAdmin.ftaGradeForm.render(user(), answer)
      Ok("TODO")
  }

  def gradeFreetextAnswers: EssentialAction = withAdmin { user => implicit request => Ok("TODO!") }

  /*
                       * views.html.questionAdmin.ftasToGrade.render(user(),
                       * FreetextAnswer.finder.all())
                       */

  def importQuizzes: EssentialAction = withAdmin { user => implicit request => Ok("TODO!") }

  def notAssignedQuestions: EssentialAction = withAdmin { user =>
    implicit request =>
      Ok(views.html.questions.questionList.render(user, List.empty /* Question.notAssignedQuestions() */))
  }

  // User

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

  //  override def correctEx(sol: StringSolution, exercise: Option[Question], user: User): Try[CompleteResult[QuestionResult]] = ???

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

  //  override def renderExercise(user: User, exercise: Question): Html = {
  //    views.html.questions.question.render(user, exercise, null /* UserAnswer.finder.byId(new UserAnswerKey(user.name, exercise.id))*/)
  //  }

  //  override def renderExesListRest: Html = ???

  override def renderResult(correctionResult: CompleteResult[QuestionResult]): Html = ???

}
