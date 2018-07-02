package model.questions

import javax.inject.Inject
import model.persistence.ExerciseCollectionTableDefs
import model.questions.QuestionConsts._
import model.questions.QuestionEnums.{Correctness, QuestionType}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class QuestionTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseCollectionTableDefs[Question, CompleteQuestion, Quiz, CompleteQuiz, Seq[GivenAnswer], QuestionSolution] {

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


  override def completeExForEx(ex: Question): Future[CompleteQuestion] =
    answersForQuestion(ex.collectionId, ex.id) map (answers => CompleteQuestion(ex, answers))

  override def completeCollForColl(coll: Quiz): Future[CompleteQuiz] =
    questionsForQuiz(coll.id) map (qs => CompleteQuiz(coll, qs))

  def completeQuizzes: Future[Seq[CompleteQuiz]] = db.run(collTable.result) map { quizSeq => quizSeq map (quiz => CompleteQuiz(quiz, Seq.empty)) }

  def completeQuiz(id: Int): Future[Option[CompleteQuiz]] = db.run(collTable.filter(_.id === id).result.headOption) flatMap {
    case None       => Future(None)
    case Some(quiz) => questionsForQuiz(id) map (questions => Some(CompleteQuiz(quiz, questions)))
  }

  private def questionsForQuiz(quizId: Int): Future[Seq[CompleteQuestion]] = db.run(exTable.filter(_.collectionId === quizId).result) flatMap {
    questionSeq => Future.sequence(questionSeq map (question => answersForQuestion(quizId, question.id) map (answers => CompleteQuestion(question, answers))))
  }

  def completeQuestion(quizId: Int, questionId: Int): Future[Option[CompleteQuestion]] =
    db.run(exTable.filter(q => q.collectionId === quizId && q.id === questionId).result.headOption) flatMap {
      case None           => Future(None)
      case Some(question) => answersForQuestion(quizId, questionId) map (answers => Some(CompleteQuestion(question, answers)))
    }

  private def answersForQuestion(quizId: Int, questionId: Int): Future[Seq[Answer]] =
    db.run(answers.filter(ans => ans.quizId === quizId && ans.questionId === questionId).result)

  // Saving

  override def saveCompleteColl(compQuiz: CompleteQuiz): Future[Boolean] = db.run(collTable insertOrUpdate compQuiz.coll) flatMap {
    _ => Future.sequence(compQuiz.exercises map saveQuestion) map (_.forall(identity))
  } recover { case _: Throwable => false }

  private def saveQuestion(compQuestion: CompleteQuestion): Future[Boolean] = db.run(exTable insertOrUpdate compQuestion.ex) flatMap {
    _ => Future.sequence(compQuestion.answers map saveAnswer) map (_.forall(identity))
  } recover { case _: Throwable => false }

  private def saveAnswer(answer: Answer): Future[Boolean] =
    db.run(answers insertOrUpdate answer) map (_ => true) recover { case _: Throwable => false }

  override def saveExerciseRest(compEx: CompleteQuestion): Future[Boolean] = ???

  // Column types

  implicit val questiontypeColumnType: BaseColumnType[QuestionType] =
    MappedColumnType.base[QuestionType, String](_.name, str => QuestionType.byString(str) getOrElse QuestionType.CHOICE)

  implicit val correctnessColumnType: BaseColumnType[Correctness] =
    MappedColumnType.base[Correctness, String](_.name, str => Correctness.byString(str) getOrElse Correctness.OPTIONAL)

  override protected implicit val solutionTypeColumnType: BaseColumnType[Seq[GivenAnswer]] = ???

  //    MappedColumnType.base[Seq[GivenAnswer], String](_.mkString, _ => Seq.empty)

  // Table defs

  class QuizzesTable(tag: Tag) extends HasBaseValuesTable[Quiz](tag, "quizzes") {

    def theme = column[String](themeName)


    def pk = primaryKey("pk", id)


    override def * = (id, title, author, text, state, theme).mapTo[Quiz]

  }

  class QuestionsTable(tag: Tag) extends ExerciseInCollectionTable[Question](tag, "questions") {

    def questionType = column[QuestionType]("question_type")

    def maxPoints = column[Int]("max_points")


    def pk = primaryKey("pk", (id, collectionId))

    def quizFk = foreignKey("quiz_fk", collectionId, collTable)(_.id)


    override def * = (id, title, author, text, state, collectionId, questionType, maxPoints).mapTo[Question]

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


    override def * = (id, questionId, quizId, ansText, correctness, explanation.?).mapTo[Answer]

  }

  class QuestionSolutionsTable(tag: Tag) extends CollectionExSolutionsTable(tag, "question_solutions") {

    def solution = column[Seq[GivenAnswer]]("todo")

    override def * = (username, collectionId, exerciseId, solution, points, maxPoints).mapTo[QuestionSolution]

  }

}