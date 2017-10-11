package controllers.questions

import java.util.Collections
import javax.inject.Inject

import controllers.core.AExerciseCollectionAdminController
import model.question.Question
import model.quiz.{Quiz, QuizReader}
import play.data.FormFactory
import play.mvc.{Result, Results}
import play.twirl.api.Html

class QuestionAdmin @Inject()(f: FormFactory)
  extends AExerciseCollectionAdminController[Question, Quiz](f, QuestionToolObject, Quiz.finder, new QuizReader()) {

  override protected def statistics: Html = new Html(
    s"""
<li>Es existieren insgesamt <a href="${controllers.questions.routes.QuestionAdmin.exercises()}">${Question.finder.query.findCount} Fragen</a> in allen Kategorien
    <ul>
    @if(!notAssignedQuestions.isEmpty) {
    <li>Es gibt noch @notAssignedQuestions.size
    <a href="@questions.routes.QuestionAdmin.notAssignedQuestions">nicht zugeordnete Fragen</a>.
    Sie k&ouml;nnen Sie <a href="@questions.routes.QuestionAdmin.assignQuestionsForm">hier</a> zuweisen.
    </li>
    }
    </ul>
    </li>
    <li>Es existieren @model.quiz.Quiz.finder.all.size <a href="@questions.routes.QuestionAdmin.exerciseCollections">Quiz(ze)</a></li>
    """)

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

  def assignQuestions: Result = {
    val form = factory.form().bindFromRequest()

    // Read it...
    //    Map<String, String> assignments = form.rawData()
    //    for(Map.Entry<String, String> entry: assignments.entrySet())
    //      assignQuestion(entry.getKey(), "on".equals(entry.getValue()))
    //
    //    return ok(views.html.questionAdmin.questionsAssigned.render(getUser(), assignments.toString()))
    Results.ok("TODO!")
  }

  def renderCollectionCreated(collections: java.util.List[model.quiz.Quiz], created: Boolean): Html = ???

  def renderExCollCreationForm(user: model.user.User, collection: model.quiz.Quiz): Html = ???

  def renderExEditForm(user: model.user.User, exercise: model.quiz.Quiz, isCreation: Boolean): Html = ???

  def renderExerciseCollections(user: model.user.User, allCollections: java.util.List[model.quiz.Quiz]): Html = ???

  def assignQuestionsForm: Result = Results.ok(views.html.questionAdmin.assignQuestionsForm.render(
    getUser, /* Question.finder.all() */ Collections.emptyList(), Quiz.finder.all()))

  def assignQuestionsSingleForm(id: Int): Result = Results.ok(views.html.questionAdmin.assignQuestionsForm.render(
    getUser, /* Question.finder.all() */ Collections.emptyList(), java.util.Arrays.asList(Quiz.finder.byId(id))))

  //  def  exportQuizzes = {
  //    val json = Json.prettyPrint(Json.toJson(Quiz.finder.all()))
  //
  //    try {
  //      File tempFile = new File("quizzes_export_" + LocalDateTime.now() + ".json")
  //      Files.asCharSink(tempFile, Charset.defaultCharset()).write(json)
  //      // false == download file!
  //      Results.ok(tempFile, false)
  //    } catch (IOException e) {
  //      Results.ok(json)
  //    }
  //  }

  def gradeFreetextAnswer(id: Int, user: String): Result = {
    // FreetextAnswer answer = FreetextAnswer.finder.byId(new
    // FreetextAnswerKey(user, id))
    //             views.html.questionAdmin.ftaGradeForm.render(getUser(), answer)
    Results.ok("TODO")
  }

  def gradeFreetextAnswers: Result = Results.ok("TODO!")

  /*
                       * views.html.questionAdmin.ftasToGrade.render(getUser(),
                       * FreetextAnswer.finder.all())
                       */

  def importQuizzes: Result = Results.ok("TODO!")

  def notAssignedQuestions: Result = Results.ok(views.html.questionList.render(
    getUser, /* Question.notAssignedQuestions() */ Question.finder.all()))

}
