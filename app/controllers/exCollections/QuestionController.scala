package controllers.exCollections

import javax.inject._

import controllers.Secured
import model.Enums.Role
import model.core._
import model.questions._
import model.{JsonFormat, User}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class QuestionController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AExCollectionController[Question, Quiz, QuestionResult, CompleteResult[QuestionResult]](cc, dbcp, r, QuestionToolObject)
    with HasDatabaseConfigProvider[JdbcProfile] with JsonFormat with Secured {

  override type SolType = String

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[String] = request.body.asJson flatMap (_.asStr)

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[String] = {
    println(request.body.asFormUrlEncoded)

    ???
  }

  // Yaml

  override type CompColl = CompleteQuiz

  override type CompEx = CompleteQuestion

  override implicit val yamlFormat: YamlFormat[CompleteQuiz] = QuestionYamlProtocol.QuizYamlFormat

  // db

  override type TQ = repo.QuizzesTable

  override def tq = repo.quizzes

  override protected def futureCompleteColls: Future[Seq[CompColl]] = repo.completeQuizzes

  override protected def futureCompleteCollById(id: Int): Future[Option[CompleteQuiz]] = repo.completeQuiz(id)

  override protected def futureCompleteExById(collId: Int, id: Int): Future[Option[CompleteQuestion]] = repo.completeQuestion(collId, id)

  override protected def saveRead(read: Seq[CompColl]): Future[Seq[Boolean]] = Future.sequence(read map repo.saveQuiz)

  // Quizzes

  //  override def correctPart(sol: StringSolution, question: Option[Question], part: String, user: User): Try[CompleteResult[EvaluationResult]]
  //  = ??? // FIXME: implement...

  //  def quiz(id: Int): EssentialAction = withUser { user => implicit request => Ok(views.html.questions.quiz.render(user, null /* Quiz.finder.byId(id)*/)) }

  //  def quizCorrection(quizId: Int, questionId: Int): EssentialAction = withUser { user =>
  //    implicit request =>
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
  //      Ok("TODO!")
  //  }


  // FIXME: stubs...

  override def correctEx(user: User, solution: SolType, exercise: CompleteQuestion, quiz: Quiz): Try[CompleteResult[QuestionResult]] = ???

  override def renderEditRest(collOpt: Option[CompleteQuiz]): Html = new Html(
    s"""<div class="form-group row">
       |  <div class="col-sm-12">
       |    <label for="${QuestionConsts.ThemeName}">Thema:</label>
       |    <input class="form-control" name="${QuestionConsts.ThemeName}" id="${QuestionConsts.ThemeName}" required ${collOpt map (coll => s"""value="${coll.coll.theme}"""") getOrElse ""})>
       |  </div>
       |</div>""".stripMargin)

  def quizStart(quizId: Int): EssentialAction = withUser { _ =>
    implicit request => Redirect(routes.QuestionController.exercise(quizId, 1))
  }

  //  def quizzes: EssentialAction = withUser { user => implicit request => Ok(views.html.questions.quizzes.render(user, null /*Quiz.finder.all.asScala.toList*/)) }

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

  def assignQuestions: EssentialAction = withAdmin { _ =>
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

  def gradeFreetextAnswer(id: Int, user: String): EssentialAction = withAdmin { _ =>
    implicit request =>
      // FreetextAnswer answer = FreetextAnswer.finder.byId(new
      // FreetextAnswerKey(user, id))
      //             views.html.questionAdmin.ftaGradeForm.render(user(), answer)
      Ok("TODO")
  }

  def gradeFreetextAnswers: EssentialAction = withAdmin { _ => implicit request => Ok("TODO!") }

  // views.html.questionAdmin.ftasToGrade.render(user(), FreetextAnswer.finder.all())

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

  def editQuestionForm(id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      futureCompleteExById(1 /*collId*/ , id) map {
        case None           => BadRequest("TODO!")
        case Some(question) =>
          if (question.author == user.username || user.stdRole == Role.RoleAdmin)
            Ok(views.html.questions.editQuestionForm.render(user, question, isEdit = true))
          else
            Redirect(routes.QuestionController.index())
      }
  }

  def newQuestion(isFreetext: Boolean): EssentialAction = withUser { _ =>
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

  //  def questionResult(id: Int): EssentialAction = withUser { user =>
  //    implicit request =>
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
  //      Ok("TODO!")
  //  }

  override def renderResult(correctionResult: CompleteResult[QuestionResult]): Html = ???

  override protected def renderExercise(user: User, quiz: Quiz, exercise: CompleteQuestion): Html =
    views.html.questions.question.render(user, quiz, exercise, null /* FIXME: old answer... UserAnswer.finder.byId(new UserAnswerKey(user.name, exercise.id))*/)

}
