package controllers.questions

import javax.inject.Inject

import controllers.excontrollers.AExerciseCollectionAdminController
import model.exercisereading.SingleReadingResult
import model.question.{Question, QuizReader}
import model.quiz.Quiz
import play.api.mvc.ControllerComponents
import play.twirl.api.Html

import scala.collection.JavaConverters._

class QuestionAdmin @Inject()(cc: ControllerComponents)
  extends AExerciseCollectionAdminController[Question, Quiz](cc, QuestionToolObject, Quiz.finder, QuizReader) {

  override protected def statistics: Html = new Html(
    s"""<li>Es existieren insgesamt <a href="${controllers.questions.routes.QuestionAdmin.exercises()}">${Question.finder.query.findCount} Fragen</a> in allen Kategorien
       |<ul>
       |@if(!notAssignedQuestions.isEmpty) {
       |<li>Es gibt noch @notAssignedQuestions.size
       |<a href="@questions.routes.QuestionAdmin.notAssignedQuestions">nicht zugeordnete Fragen</a>.
       |Sie k&ouml;nnen Sie <a href="@questions.routes.QuestionAdmin.assignQuestionsForm">hier</a> zuweisen.
       |</li>
       |}
       |</ul>
       |</li>
       |<li>Es existieren @model.quiz.Quiz.finder.all.size <a href="@questions.routes.QuestionAdmin.exerciseCollections">Quiz(ze)</a></li>""".stripMargin)

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

  def assignQuestions = Action { implicit request =>
    //    val form = factory.form().bindFromRequest()

    // Read it...
    //    Map<String, String> assignments = form.rawData()
    //    for(Map.Entry<String, String> entry: assignments.entrySet())
    //      assignQuestion(entry.getKey(), "on".equals(entry.getValue()))
    //
    //    return ok(views.html.questionAdmin.questionsAssigned.render(getUser(), assignments.toString()))
    Ok("TODO!")
  }

  override def renderCollectionCreated(collections: List[SingleReadingResult[Quiz]]): Html = ???

  override def renderExCollCreationForm(user: model.user.User, collection: model.quiz.Quiz): Html = ???

  override def renderExEditForm(user: model.user.User, exercise: model.quiz.Quiz, isCreation: Boolean): Html = ???

  override def renderExerciseCollections(user: model.user.User, allCollections: List[model.quiz.Quiz]): Html = ???

  def assignQuestionsForm = Action { implicit request =>
    Ok(views.html.questionAdmin.assignQuestionsForm.render(getUser, /* Question.finder.all() */ List.empty, Quiz.finder.all.asScala.toList))
  }

  def assignQuestionsSingleForm(id: Int) = Action { implicit request =>
    Ok(views.html.questionAdmin.assignQuestionsForm.render(getUser, /* Question.finder.all() */ List.empty, List(Quiz.finder.byId(id))))
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

  def gradeFreetextAnswer(id: Int, user: String) = Action { implicit request =>
    // FreetextAnswer answer = FreetextAnswer.finder.byId(new
    // FreetextAnswerKey(user, id))
    //             views.html.questionAdmin.ftaGradeForm.render(getUser(), answer)
    Ok("TODO")
  }

  def gradeFreetextAnswers = Action { implicit request => Ok("TODO!") }

  /*
                       * views.html.questionAdmin.ftasToGrade.render(getUser(),
                       * FreetextAnswer.finder.all())
                       */

  def importQuizzes = Action { implicit request => Ok("TODO!") }

  def notAssignedQuestions = Action { implicit request =>
    Ok(views.html.questionList.render(getUser, /* Question.notAssignedQuestions() */ Question.finder.all.asScala.toList))
  }

}
