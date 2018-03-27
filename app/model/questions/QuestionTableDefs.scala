package model.questions

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.persistence.ExerciseCollectionTableDefs
import model.questions.QuestionConsts._
import model.questions.QuestionEnums.{Correctness, QuestionType}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
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

case class CompleteQuiz(coll: Quiz, exercises: Seq[CompleteQuestion]) extends CompleteCollection {

  override type Ex = Question

  override type CompEx = CompleteQuestion

  override type Coll = Quiz

  override def renderRest: Html = new Html("") // FIXME: implement! ???

  override def exercisesWithFilter(filter: String): Seq[CompleteQuestion] = QuestionType.byString(filter) match {
    case Some(questionType) => exercises filter (_.ex.questionType == questionType)
    case None               => exercises
  }

}

case class CompleteQuestion(ex: Question, answers: Seq[Answer]) extends CompleteExInColl[Question] {

  def givenAnswers: Seq[Answer] = Seq.empty

  def answersForTemplate: Seq[Answer] = scala.util.Random.shuffle(answers)

  def getCorrectAnswers: Seq[Answer] = answers filter (_.isCorrect)

  def userHasAnswered(username: String) = false

  override def preview: Html = new Html(
    s"""<p><b>Antworten:</b></p>
       |<div class="row">${answers map previewAnswer mkString}</div>""".stripMargin)

  private def previewAnswer(answer: Answer): String =
    s"""<div class="col-md-2">${answer.correctness.name}</div>
       |<div class="col-md-10">${answer.text} </div>""".stripMargin

}

// Case classes for db

case class Quiz(id: Int, title: String, author: String, text: String, state: ExerciseState, theme: String)
  extends ExerciseCollection[Question, CompleteQuestion] {

  def this(baseValues: (Int, String, String, String, ExerciseState), theme: String) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, theme)

}


case class Question(id: Int, title: String, author: String, text: String, state: ExerciseState,
                    collectionId: Int, questionType: QuestionType, maxPoints: Int) extends ExInColl {

  def this(baseValues: (Int, String, String, String, ExerciseState), collectionId: Int, questionType: QuestionType, maxPoints: Int) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, collectionId, questionType, maxPoints)

  def isFreetext: Boolean = questionType == QuestionType.FREETEXT

}

case class Answer(id: Int, questionId: Int, quizId: Int, text: String, correctness: Correctness, explanation: Option[String]) extends IdAnswer {

  def isCorrect: Boolean = correctness != Correctness.WRONG

}

case class QuestionRating(questionId: Int, userName: String, rating: Int)

case class UserAnswer(questionId: Int, userName: String, text: String)

case class QuestionSolution(username: String, collectionId: Int, exerciseId: Int, answers: Seq[GivenAnswer]) extends CollectionExSolution

// Table Definitions

class QuestionsTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseCollectionTableDefs[Question, CompleteQuestion, Quiz, CompleteQuiz, QuestionSolution] {

  import profile.api._

  // Table types

  override protected type ExTableDef = QuestionsTable

  override protected type CollTableDef = QuizzesTable

  override protected type SolTableDef = QuestionSolutionsTable

  // Table queries

  override protected val exTable = TableQuery[QuestionsTable]

  override protected val collTable = TableQuery[QuizzesTable]

  override protected val solTable = TableQuery[QuestionSolutionsTable]

  private val answers = TableQuery[AnswersTable]

  // Reading


  override def completeExForEx(ex: Question)(implicit ec: ExecutionContext): Future[CompleteQuestion] =
    answersForQuestion(ex.collectionId, ex.id) map (answers => CompleteQuestion(ex, answers))

  override def completeCollForColl(coll: Quiz)(implicit ec: ExecutionContext): Future[CompleteQuiz] =
    questionsForQuiz(coll.id) map (qs => CompleteQuiz(coll, qs))

  def completeQuizzes(implicit ec: ExecutionContext): Future[Seq[CompleteQuiz]] = db.run(collTable.result) map { quizSeq => quizSeq map (quiz => CompleteQuiz(quiz, Seq.empty)) }

  def completeQuiz(id: Int)(implicit ec: ExecutionContext): Future[Option[CompleteQuiz]] = db.run(collTable.filter(_.id === id).result.headOption) flatMap {
    case None       => Future(None)
    case Some(quiz) => questionsForQuiz(id) map (questions => Some(CompleteQuiz(quiz, questions)))
  }

  private def questionsForQuiz(quizId: Int)(implicit ec: ExecutionContext): Future[Seq[CompleteQuestion]] = db.run(exTable.filter(_.collectionId === quizId).result) flatMap {
    questionSeq => Future.sequence(questionSeq map (question => answersForQuestion(quizId, question.id) map (answers => CompleteQuestion(question, answers))))
  }

  def completeQuestion(quizId: Int, questionId: Int)(implicit ec: ExecutionContext): Future[Option[CompleteQuestion]] =
    db.run(exTable.filter(q => q.collectionId === quizId && q.id === questionId).result.headOption) flatMap {
      case None           => Future(None)
      case Some(question) => answersForQuestion(quizId, questionId) map (answers => Some(CompleteQuestion(question, answers)))
    }

  private def answersForQuestion(quizId: Int, questionId: Int)(implicit ec: ExecutionContext): Future[Seq[Answer]] =
    db.run(answers.filter(ans => ans.quizId === quizId && ans.questionId === questionId).result)

  // Saving

  override def saveCompleteColl(compQuiz: CompleteQuiz)(implicit ec: ExecutionContext): Future[Boolean] = db.run(collTable insertOrUpdate compQuiz.coll) flatMap {
    _ => Future.sequence(compQuiz.exercises map saveQuestion) map (_.forall(identity))
  } recover { case _: Throwable => false }

  private def saveQuestion(compQuestion: CompleteQuestion)(implicit ec: ExecutionContext): Future[Boolean] = db.run(exTable insertOrUpdate compQuestion.ex) flatMap {
    _ => Future.sequence(compQuestion.answers map saveAnswer) map (_.forall(identity))
  } recover { case _: Throwable => false }

  private def saveAnswer(answer: Answer)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(answers insertOrUpdate answer) map (_ => true) recover { case _: Throwable => false }

  override def saveExerciseRest(compEx: CompleteQuestion)(implicit ec: ExecutionContext): Future[Boolean] = ???

  // Column types

  implicit val questiontypeColumnType: BaseColumnType[QuestionType] =
    MappedColumnType.base[QuestionType, String](_.name, str => QuestionType.byString(str) getOrElse QuestionType.CHOICE)

  implicit val correctnessColumnType: BaseColumnType[Correctness] =
    MappedColumnType.base[Correctness, String](_.name, str => Correctness.byString(str) getOrElse Correctness.OPTIONAL)

  implicit val givenAnswerColumnType: BaseColumnType[Seq[GivenAnswer]] =
    MappedColumnType.base[Seq[GivenAnswer], String](_.mkString, _ => Seq.empty)

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

    def quizFk = foreignKey("quiz_fk", collectionId, collTable)(_.id)


    def * = (id, title, author, text, state, collectionId, questionType, maxPoints) <> (Question.tupled, Question.unapply)

  }

  class AnswersTable(tag: Tag) extends Table[Answer](tag, "question_answers") {

    def id = column[Int](idName)

    def questionId = column[Int]("question_id")

    def quizId = column[Int]("collection_id")

    def ansText = column[String]("answer_text")

    def correctness = column[Correctness]("correctness")

    def explanation = column[String]("explanation")


    def pk = primaryKey("pk", (id, questionId, quizId))

    def questionFk = foreignKey("question_fk", (questionId, quizId), exTable)(question => (question.id, question.collectionId))


    def * = (id, questionId, quizId, ansText, correctness, explanation.?) <> (Answer.tupled, Answer.unapply)

  }

  class QuestionSolutionsTable(tag: Tag) extends CollectionExSolutionsTable[QuestionSolution](tag, "question_solutions") {

    def givenAnswers = column[Seq[GivenAnswer]]("todo")

    def * = (username, collectionId, exerciseId, givenAnswers) <> (QuestionSolution.tupled, QuestionSolution.unapply)

  }

}