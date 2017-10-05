package controllers.questions

import java.util.{ Arrays, Collections }

import controllers.core.{ AExerciseCollectionAdminController, BaseController }
import javax.inject.Inject
import model.question.Question
import model.quiz.Quiz
import model.user.User
import play.data.FormFactory
import play.mvc.Results
import model.quiz.QuizReader

class QuestionAdmin @Inject() (f: FormFactory)
  extends AExerciseCollectionAdminController[Question, Quiz](f, Quiz.finder, new QuizReader()) {

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

  override def renderAdminIndex(user: User) = views.html.questionAdmin.index.render(user)

  def assignQuestions = {
    val form = factory.form().bindFromRequest()

    // Read it...
    //    Map<String, String> assignments = form.rawData()
    //    for(Map.Entry<String, String> entry: assignments.entrySet())
    //      assignQuestion(entry.getKey(), "on".equals(entry.getValue()))
    //
    //    return ok(views.html.questionAdmin.questionsAssigned.render(getUser(), assignments.toString()))
    Results.ok("TODO!")
  }

  def renderCollectionCreated(collections: java.util.List[model.quiz.Quiz], created: Boolean): play.twirl.api.Html = ???

  def renderExCollCreationForm(user: model.user.User, collection: model.quiz.Quiz): play.twirl.api.Html = ???

  def renderExEditForm(user: model.user.User, exercise: model.quiz.Quiz, isCreation: Boolean): play.twirl.api.Html = ???

  def renderExerciseCollections(user: model.user.User, allCollections: java.util.List[model.quiz.Quiz]): play.twirl.api.Html = ???

  def assignQuestionsForm = Results.ok(views.html.questionAdmin.assignQuestionsForm.render(
    BaseController.getUser,
    /* Question.finder.all() */ Collections.emptyList(), Quiz.finder.all()))

  def assignQuestionsSingleForm(id: Int) = Results.ok(views.html.questionAdmin.assignQuestionsForm.render(
    BaseController.getUser,
    /* Question.finder.all() */ Collections.emptyList(), Arrays.asList(Quiz.finder.byId(id))))

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

  def gradeFreetextAnswer(id: Int, user: String) = {
    // FreetextAnswer answer = FreetextAnswer.finder.byId(new
    // FreetextAnswerKey(user, id))
    //             views.html.questionAdmin.ftaGradeForm.render(getUser(), answer)
    Results.ok("TODO")
  }

  def gradeFreetextAnswers = Results.ok("TODO!")
  /*
                       * views.html.questionAdmin.ftasToGrade.render(getUser(),
                       * FreetextAnswer.finder.all())
                       */

  def importQuizzes = Results.ok("TODO!")

  def notAssignedQuestions = Results.ok(views.html.questionList.render(
    BaseController.getUser, /* Question.notAssignedQuestions() */ Question.finder.all()))

}
