package model.questions

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import model.Enums.ExerciseState
import model.core.ExerciseCollection
import model.questions.QuestionConsts._
import model.questions.QuestionEnums.{Correctness, QuestionType}
import model.{CompleteEx, Exercise, TableDefs}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

object QuestionHelper {

  val MIN_ANSWERS = 2
  val STD_ANSWERS = 4
  val MAX_ANSWERS = 8
}

case class QuizCompleteEx(ex: Quiz) extends CompleteEx[Quiz]

case class Quiz(i: Int, ti: String, a: String, te: String, s: ExerciseState, theme: String)
  extends ExerciseCollection[Question](i, ti, a, te, s) {
  override def exercises: List[Question] = List.empty
}

case class Question(i: Int, ti: String, a: String, te: String, s: ExerciseState,
                    @JsonProperty(value = "exerciseType", required = true) questionType: QuestionType)
  extends Exercise(i, ti, a, te, s) {

  def answers: List[Answer] = List.empty

  def givenAnswers: List[Answer] = List.empty

  def quizzes: List[Quiz] = List.empty

  def maxPoints = 0

  @JsonIgnore
  def answersForTemplate: List[Answer] = scala.util.Random.shuffle(answers)

  @JsonIgnore
  def getCorrectAnswers: List[Answer] = answers.filter(_.isCorrect)

  @JsonIgnore
  def isFreetext: Boolean = questionType == QuestionType.FREETEXT

  def userHasAnswered(username: String) = false

}


case class Answer(id: Int, questionId: Int, text: String, correctness: Correctness) {

  @JsonIgnore
  def getIdAsChar: Char = ('a' + id - 1).toChar

  @JsonIgnore
  def isCorrect: Boolean = correctness != Correctness.WRONG

}

case class QuestionRating(questionId: Int, userName: String, rating: Int)

case class UserAnswer(questionId: Int, userName: String, text: String)

trait QuestionsTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val quizzes = TableQuery[QuizzesTable]

  val questions = TableQuery[QuestionsTable]

  implicit val questiontypeColumnType: BaseColumnType[QuestionType] =
    MappedColumnType.base[QuestionType, String](_.name, str => Option(QuestionType.valueOf(str)).getOrElse(QuestionType.CHOICE))

  class QuizzesTable(tag: Tag) extends HasBaseValuesTable[Quiz](tag, "quizzes") {

    def theme = column[String](THEME_NAME)


    def * = (id, title, author, text, state, theme) <> (Quiz.tupled, Quiz.unapply)

  }

  class QuestionsTable(tag: Tag) extends HasBaseValuesTable[Question](tag, "questions") {

    def questionType = column[QuestionType]("question_type")


    def * = (id, title, author, text, state, questionType) <> (Question.tupled, Question.unapply)

  }

}