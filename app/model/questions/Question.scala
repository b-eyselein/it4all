package model.questions

import enumeratum.{EnumEntry, PlayEnum}
import model._
import play.twirl.api.Html

import scala.collection.immutable.IndexedSeq
import scala.language.postfixOps

object QuestionHelper {

  val MinAnswers: Int = 2
  val StdAnswers: Int = 4
  val MaxAnswers: Int = 8

}

sealed abstract class QuestionType(val german: String) extends EnumEntry

object QuestionTypes extends PlayEnum[QuestionType] {

  override val values: IndexedSeq[QuestionType] = findValues


  case object CHOICE extends QuestionType("Auswahlfrage")

  case object FREETEXT extends QuestionType("Freitextfrage")

}


sealed abstract class Correctness(val title: String, val bsButtonColor: String) extends EnumEntry

object Correctnesses extends PlayEnum[Correctness] {

  val values: IndexedSeq[Correctness] = findValues


  case object CORRECT extends Correctness("Korrekt", "success")

  case object OPTIONAL extends Correctness("Optional", "warning")

  case object WRONG extends Correctness("Falsch", "danger")

}


// Classes for use

final case class CompleteQuiz(coll: Quiz, exercises: Seq[CompleteQuestion]) extends CompleteCollection {

  override type Ex = Question

  override type CompEx = CompleteQuestion

  override type Coll = Quiz

  override def renderRest: Html = new Html("") // FIXME: implement! ???

  override def exercisesWithFilter(filter: String): Seq[CompleteQuestion] = QuestionTypes.withNameInsensitiveOption(filter) match {
    case Some(questionType) => exercises filter (_.ex.questionType == questionType)
    case None               => exercises
  }

}

final case class CompleteQuestion(ex: Question, answers: Seq[Answer]) extends CompleteExInColl[Question] {

  def givenAnswers: Seq[Answer] = Seq[Answer]()

  def answersForTemplate: Seq[Answer] = scala.util.Random.shuffle(answers)

  def getCorrectAnswers: Seq[Answer] = answers filter (_.isCorrect)

  def userHasAnswered(username: String): Boolean = false

  override def preview: Html = new Html(
    s"""<p><b>Antworten:</b></p>
       |<div class="row">${answers map previewAnswer mkString}</div>""".stripMargin)

  private def previewAnswer(answer: Answer): String =
    s"""<div class="col-md-2">${answer.correctness.entryName}</div>
       |<div class="col-md-10">${answer.text} </div>""".stripMargin

}

// Case classes for db

final case class Quiz(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                      theme: String) extends ExerciseCollection[Question, CompleteQuestion]

final case class Question(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                          collectionId: Int, collSemVer: SemanticVersion, questionType: QuestionType, maxPoints: Int) extends ExInColl {

  def isFreetext: Boolean = questionType == QuestionTypes.FREETEXT

}

final case class Answer(id: Int, exerciseId: Int, exSemVer: SemanticVersion, collId: Int, collSemVer: SemanticVersion,
                        text: String, correctness: Correctness, explanation: Option[String]) extends IdAnswer {

  def isCorrect: Boolean = correctness != Correctnesses.WRONG

}

final case class QuestionRating(questionId: Int, exSemVer: SemanticVersion, collId: Int, collSemVer: SemanticVersion, userName: String, rating: Int)

final case class UserAnswer(questionId: Int, exSemVer: SemanticVersion, collId: Int, collSemVer: SemanticVersion, userName: String, text: String)

final case class QuestionSolution(id: Int, username: String, exerciseId: Int, exSemVer: SemanticVersion, collectionId: Int,
                                  collSemVer: SemanticVersion, solution: Seq[GivenAnswer], points: Points, maxPoints: Points)
  extends CollectionExSolution[Seq[GivenAnswer]]
