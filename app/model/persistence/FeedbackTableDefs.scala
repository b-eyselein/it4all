package model.persistence

import model._
import model.core.CoreConsts._
import model.feedback.{Feedback, Mark}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.json.{JsValue, Json}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}

trait FeedbackTableDefs extends TableDefs with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  implicit val executionContext: ExecutionContext

  // Table queries

  private val feedbackTQ = TableQuery[FeedbackTable]

  // Queries

  def futureAllEvaluatedTools: Future[Seq[String]] = db.run(
    feedbackTQ
      .map(_.toolUrlPart)
      .distinct
      .result
  )

  def futureMaybeFeedback(user: User, toolUrlPart: String): Future[Option[Feedback]] = db.run(
    feedbackTQ
      .filter(fb => fb.username === user.username && fb.toolUrlPart === toolUrlPart)
      .result
      .headOption
      .map {
        case None => None
        case Some((_, _, targetDegree, subject, semester, marksJson, comment)) =>
          Some(Feedback(targetDegree, subject, semester, ???, comment))
      }
  )

  def futureAllEvaluationResultsForTool(toolUrlPart: String): Future[Seq[Feedback]] = db.run(
    feedbackTQ
      .filter(_.toolUrlPart === toolUrlPart)
      .result
      .map(
        dbFeedbacks =>
          dbFeedbacks.map {
            case (_, _, targetDegree, subject, semester, marksJson, comment) =>
              Feedback(targetDegree, subject, semester, ???, comment)
          }
      )
  )

  def futureUpsertFeedback(username: String, toolUrl: String, feedback: Feedback): Future[Boolean] =
    db.run(
        feedbackTQ.insertOrUpdate(
          (
            username,
            toolUrl,
            feedback.targetDegree,
            feedback.subject,
            feedback.semester,
            feedback.marksJson,
            feedback.comment
          )
        )
      )
      .transform(_ == 1, identity)

  // Column types

  private implicit val jsValueColumnType: BaseColumnType[JsValue] =
    MappedColumnType.base[JsValue, String](Json.stringify, Json.parse)

  private implicit val markColumnType: BaseColumnType[Mark] =
    MappedColumnType.base[Mark, String](_.entryName, str => Mark.withNameInsensitiveOption(str) getOrElse Mark.NoMark)

  // Tables

  class FeedbackTable(tag: Tag)
      extends Table[(String, String, Option[String], Option[String], Option[Int], JsValue, String)](tag, "feedback") {

    def username: Rep[String] = column[String]("username")

    def toolUrlPart: Rep[String] = column[String]("tool_url")

    def targetDegree: Rep[Option[String]] = column[Option[String]](targetDegreeName)

    def subject: Rep[Option[String]] = column[Option[String]](subjectName)

    def semester: Rep[Option[Int]] = column[Option[Int]](semesterName)

    def marks: Rep[JsValue] = column[JsValue]("marks_json")

    def comment: Rep[String] = column[String]("comment")

    def pk: PrimaryKey = primaryKey("pk", (username, toolUrlPart))

    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)

    override def * : ProvenShape[(String, String, Option[String], Option[String], Option[Int], JsValue, String)] =
      (username, toolUrlPart, targetDegree, subject, semester, marks, comment)

  }

}
