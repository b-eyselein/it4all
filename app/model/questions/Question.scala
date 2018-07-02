package model.questions

import model._
import model.questions.QuestionEnums.{Correctness, QuestionType}
import play.twirl.api.Html

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
  extends ExerciseCollection[Question, CompleteQuestion]

case class Question(id: Int, title: String, author: String, text: String, state: ExerciseState,
                    collectionId: Int, questionType: QuestionType, maxPoints: Int) extends ExInColl {

  def isFreetext: Boolean = questionType == QuestionType.FREETEXT

}

case class Answer(id: Int, questionId: Int, quizId: Int, text: String, correctness: Correctness, explanation: Option[String]) extends IdAnswer {

  def isCorrect: Boolean = correctness != Correctness.WRONG

}

case class QuestionRating(questionId: Int, userName: String, rating: Int)

case class UserAnswer(questionId: Int, userName: String, text: String)

case class QuestionSolution(username: String, collectionId: Int, exerciseId: Int, solution: Seq[GivenAnswer],
                            points: Double, maxPoints: Double) extends CollectionExSolution[Seq[GivenAnswer]]
