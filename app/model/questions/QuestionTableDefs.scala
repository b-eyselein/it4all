package model.questions

import javax.inject.Inject
import model.SemanticVersion
import model.persistence.ExerciseCollectionTableDefs
import model.questions.QuestionConsts._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

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

  def completeQuizzes: Future[Seq[CompleteQuiz]] = db.run(collTable.result) map { quizSeq => quizSeq map (quiz => CompleteQuiz(quiz, Seq[CompleteQuestion]())) }

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
    db.run(answers.filter(ans => ans.collId === quizId && ans.exerciseId === questionId).result)

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
    MappedColumnType.base[QuestionType, String](_.entryName, QuestionTypes.withNameInsensitive)

  implicit val correctnessColumnType: BaseColumnType[Correctness] =
    MappedColumnType.base[Correctness, String](_.entryName, Correctnesses.withNameInsensitive)

  override protected implicit val solutionTypeColumnType: BaseColumnType[Seq[GivenAnswer]] = // FIXME!: ???
    MappedColumnType.base[Seq[GivenAnswer], String](_.mkString, _ => Seq[GivenAnswer]())

  // Table defs

  class QuizzesTable(tag: Tag) extends ExerciseCollectionTable(tag, "quizzes") {

    def theme: Rep[String] = column[String](themeName)


    override def * : ProvenShape[Quiz] = (id, semanticVersion, title, author, text, state, theme) <> (Quiz.tupled, Quiz.unapply)

  }

  class QuestionsTable(tag: Tag) extends ExerciseInCollectionTable(tag, "questions") {

    def questionType: Rep[QuestionType] = column[QuestionType]("question_type")

    def maxPoints: Rep[Int] = column[Int]("max_points")


    override def * : ProvenShape[Question] = (id, semanticVersion, title, author, text, state, collectionId, collSemVer, questionType, maxPoints) <> (Question.tupled, Question.unapply)

  }

  class AnswersTable(tag: Tag) extends Table[Answer](tag, "question_answers") {

    def id: Rep[Int] = column[Int](idName)

    def exerciseId: Rep[Int] = column[Int]("question_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def collId: Rep[Int] = column[Int]("collection_id")

    def collSemVer: Rep[SemanticVersion] = column[SemanticVersion]("coll_sem_ver")

    def ansText: Rep[String] = column[String]("answer_text")

    def correctness: Rep[Correctness] = column[Correctness]("correctness")

    def explanation: Rep[String] = column[String]("explanation")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, collId))

    def questionFk: ForeignKeyQuery[QuestionsTable, Question] = foreignKey("question_fk", (exerciseId, collId), exTable)(question => (question.id, question.collectionId))


    override def * : ProvenShape[Answer] = (id, exerciseId, exSemVer, collId, collSemVer, ansText, correctness, explanation.?) <> (Answer.tupled, Answer.unapply)

  }

  class QuestionSolutionsTable(tag: Tag) extends CollectionExSolutionsTable(tag, "question_solutions") {

    def solution: Rep[Seq[GivenAnswer]] = column[Seq[GivenAnswer]]("answers")


    override def * : ProvenShape[QuestionSolution] = (username, exerciseId, exSemVer, collectionId, collSemVer, solution, points, maxPoints) <> (QuestionSolution.tupled, QuestionSolution.unapply)

  }

}