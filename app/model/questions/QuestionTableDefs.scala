package model.questions

import javax.inject.Inject

import model.Enums.ExerciseState
import model._
import model.questions.QuestionConsts._
import model.questions.QuestionEnums.{Correctness, QuestionType}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Call
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

object QuestionHelper {

  val MIN_ANSWERS = 2
  val STD_ANSWERS = 4
  val MAX_ANSWERS = 8

}

// Classes for use

class CompleteQuizWrapper(override val coll: CompleteQuiz) extends CompleteCollectionWrapper {

  override type Ex = Question

  override type CompEx = CompleteQuestion

  override type Coll = Quiz

  override type CompColl = CompleteQuiz

}

case class CompleteQuiz(coll: Quiz, exercises: Seq[CompleteQuestion]) extends CompleteCollection[Question, CompleteQuestion, Quiz] {

  override def renderRest: Html = new Html("") // FIXME: implement! ???

  override def exercisesWithFilter(filter: String): Seq[CompleteQuestion] = QuestionType.byString(filter) match {
    case Some(questionType) => exercises filter (_.ex.questionType == questionType)
    case None               => exercises
  }

}

case class CompleteQuestion(ex: Question, answers: Seq[Answer]) extends CompleteEx[Question] {

  def givenAnswers: Seq[Answer] = Seq.empty

  def answersForTemplate: Seq[Answer] = scala.util.Random.shuffle(answers)

  def getCorrectAnswers: Seq[Answer] = answers filter (_.isCorrect)

  def userHasAnswered(username: String) = false

  override def exType: String = ex.questionType.name

  override def preview: Html = new Html(
    s"""<p><b>Antworten:</b></p>
       |<div class="row">${answers map previewAnswer mkString}</div>""".stripMargin)

  private def previewAnswer(answer: Answer): String =
    s"""<div class="col-md-2">${answer.correctness.name}</div>
       |<div class="col-md-10">${answer.text} </div>""".stripMargin

  override def exerciseRoutes: Map[Call, String] = ???

}

// Case classes for db

object Quiz {

  def tupled(t: (Int, String, String, String, ExerciseState, String)): Quiz =
    Quiz(t._1, t._2, t._3, t._4, t._5, t._6)

  def apply(i: Int, ti: String, a: String, te: String, s: ExerciseState, theme: String) =
    new Quiz(BaseValues(i, ti, a, te, s), theme)

  def unapply(arg: Quiz): Option[(Int, String, String, String, ExerciseState, String)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.theme))

}

case class Quiz(baseValues: BaseValues, theme: String) extends ExerciseCollection[Question, CompleteQuestion]

object Question {

  def tupled(t: (Int, String, String, String, ExerciseState, Int, QuestionType, Int)): Question =
    Question(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)

  def apply(i: Int, ti: String, a: String, te: String, s: ExerciseState, quizId: Int, questionType: QuestionType, maxPoints: Int) =
    new Question(BaseValues(i, ti, a, te, s), quizId, questionType, maxPoints)

  def unapply(arg: Question): Option[(Int, String, String, String, ExerciseState, Int, QuestionType, Int)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.collectionId, arg.questionType, arg.maxPoints))

}

case class Question(override val baseValues: BaseValues, collectionId: Int, questionType: QuestionType, maxPoints: Int) extends ExerciseInCollection {

  def isFreetext: Boolean = questionType == QuestionType.FREETEXT

}

case class Answer(id: Int, questionId: Int, quizId: Int, text: String, correctness: Correctness, explanation: Option[String]) extends IdAnswer {

  def isCorrect: Boolean = correctness != Correctness.WRONG

}

case class QuestionRating(questionId: Int, userName: String, rating: Int)

case class UserAnswer(questionId: Int, userName: String, text: String)

class QuestionsTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseCollectionTableDefs[Question, CompleteQuestion, Quiz, CompleteQuiz] {

  import profile.api._

  // Reading

  def questionsInQuiz(id: Int): Future[Int] = db.run(questions.filter(_.collectionId === id).size.result)

  def completeQuizzes(implicit ec: ExecutionContext): Future[Seq[CompleteQuiz]] = db.run(quizzes.result) map { quizSeq => quizSeq map (quiz => CompleteQuiz(quiz, Seq.empty)) }

  def completeQuiz(id: Int)(implicit ec: ExecutionContext): Future[Option[CompleteQuiz]] = db.run(quizzes.filter(_.id === id).result.headOption) flatMap {
    case None       => Future(None)
    case Some(quiz) => questionsForQuiz(id) map (questions => Some(CompleteQuiz(quiz, questions)))
  }

  private def questionsForQuiz(quizId: Int)(implicit ec: ExecutionContext): Future[Seq[CompleteQuestion]] = db.run(questions.filter(_.collectionId === quizId).result) flatMap {
    questionSeq => Future.sequence(questionSeq map (question => answersForQuestion(quizId, question.id) map (answers => CompleteQuestion(question, answers))))
  }

  def completeQuestion(quizId: Int, questionId: Int)(implicit ec: ExecutionContext): Future[Option[CompleteQuestion]] =
    db.run(questions.filter(q => q.collectionId === quizId && q.id === questionId).result.headOption) flatMap {
      case None           => Future(None)
      case Some(question) => answersForQuestion(quizId, questionId) map (answers => Some(CompleteQuestion(question, answers)))
    }

  private def answersForQuestion(quizId: Int, questionId: Int)(implicit ec: ExecutionContext): Future[Seq[Answer]] =
    db.run(answers.filter(ans => ans.quizId === quizId && ans.questionId === questionId).result)

  // Saving

  def saveQuiz(compQuiz: CompleteQuiz)(implicit ec: ExecutionContext): Future[Boolean] = db.run(quizzes insertOrUpdate compQuiz.coll) flatMap {
    _ => Future.sequence(compQuiz.exercises map saveQuestion) map (_.forall(identity))
  } recover { case _: Throwable => false }

  def saveQuestion(compQuestion: CompleteQuestion)(implicit ec: ExecutionContext): Future[Boolean] = db.run(questions insertOrUpdate compQuestion.ex) flatMap {
    _ => Future.sequence(compQuestion.answers map saveAnswer) map (_.forall(identity))
  } recover { case _: Throwable => false }

  def saveAnswer(answer: Answer)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(answers insertOrUpdate answer) map (_ => true) recover { case _: Throwable => false }

  // Table queries

  val quizzes = TableQuery[QuizzesTable]

  val questions = TableQuery[QuestionsTable]


  override type ExTableDef = QuestionsTable

  override type CollTableDef = QuizzesTable

  override val exTable = questions

  override val collTable = quizzes

  override def completeExForEx(ex: Question)(implicit ec: ExecutionContext): Future[CompleteQuestion] =
    answersForQuestion(ex.collectionId, ex.id) map (answers => CompleteQuestion(ex, answers))

  override def completeCollForColl(coll: Quiz)(implicit ec: ExecutionContext): Future[CompleteQuiz] =
    questionsForQuiz(coll.id) map (qs => CompleteQuiz(coll, qs))


  val answers = TableQuery[AnswersTable]

  // Column types

  implicit val questiontypeColumnType: BaseColumnType[QuestionType] =
    MappedColumnType.base[QuestionType, String](_.name, str => QuestionType.byString(str) getOrElse QuestionType.CHOICE)

  implicit val correctnessColumnType: BaseColumnType[Correctness] =
    MappedColumnType.base[Correctness, String](_.name, str => Correctness.byString(str) getOrElse Correctness.OPTIONAL)

  // Table defs

  class QuizzesTable(tag: Tag) extends HasBaseValuesTable[Quiz](tag, "quizzes") {

    def theme = column[String](ThemeName)


    def pk = primaryKey("pk", id)


    def * = (id, title, author, text, state, theme) <> (Quiz.tupled, Quiz.unapply)

  }

  class QuestionsTable(tag: Tag) extends ExerciseInCollectionTable[Question](tag, "questions") {

    def questionType = column[QuestionType]("question_type")

    def maxPoints = column[Int]("max_points")


    def pk = primaryKey("pk", (id, collectionId))

    def quizFk = foreignKey("quiz_fk", collectionId, quizzes)(_.id)


    def * = (id, title, author, text, state, collectionId, questionType, maxPoints) <> (Question.tupled, Question.unapply)

  }


  class AnswersTable(tag: Tag) extends Table[Answer](tag, "question_answers") {

    def id = column[Int](ID_NAME)

    def questionId = column[Int]("question_id")

    def quizId = column[Int]("collection_id")

    def ansText = column[String]("answer_text")

    def correctness = column[Correctness]("correctness")

    def explanation = column[String]("explanation")


    def pk = primaryKey("pk", (id, questionId, quizId))

    def questionFk = foreignKey("question_fk", (questionId, quizId), questions)(question => (question.id, question.collectionId))


    def * = (id, questionId, quizId, ansText, correctness, explanation.?) <> (Answer.tupled, Answer.unapply)

  }

}