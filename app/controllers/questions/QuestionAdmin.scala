package controllers.questions

import javax.inject._

import controllers.core.excontrollers.AExerciseCollectionAdminController
import model.User
import model.core.{Repository, Secured, SingleReadingResult}
import model.questions._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Reads
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

class QuestionAdmin @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AExerciseCollectionAdminController[Question, Quiz](cc, dbcp, r, QuestionToolObject) with Secured {

  override def reads: Reads[Quiz] = null // QuestionReads.questionReads

  override type TQ = repo.QuizzesTable

  override def tq = repo.quizzes

  override protected def statistics: Future[Html] = Future(new Html(""))

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

  override def renderCollectionCreated(collections: List[SingleReadingResult[Quiz]]): Html = ???

  override def renderExCollCreationForm(user: User, collection: Quiz): Html = ???

  override def renderExEditForm(user: User, exercise: Quiz, isCreation: Boolean): Html = ???

  override def renderExerciseCollections(user: User, allCollections: List[Quiz]): Html = ???

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

}
