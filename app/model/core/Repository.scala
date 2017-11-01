package model.core

import javax.inject._

import model.blanks.BlanksExercises
import model.ebnf.EbnfExercises
import model.mindmap.MindmapExercises
import model.programming.ProgExercises
import model.questions.QuestionsTableDefs
import model.spread.SpreadExercises
import model.sql.SqlTableDefs
import model.uml.UmlExercises
import model.web.WebExercises
import model.xml.XmlExercises
import model.{Course, TableDefs, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class Repository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with TableDefs

    with BlanksExercises with EbnfExercises with MindmapExercises with ProgExercises
    with QuestionsTableDefs with SpreadExercises with SqlTableDefs
    with UmlExercises with WebExercises with XmlExercises {

  import profile.api._

  def userByName(username: String): Future[Option[User]] = db.run(users.findBy(_.username).apply(username).result.headOption)

  def allUsers: Future[Seq[User]] = db.run(users.result)

  def numOfUsers: Future[Int] = db.run(users.size.result)

  def allCourses: Future[Seq[Course]] = db.run(courses.result)

  def numOfCourses: Future[Int] = db.run(courses.size.result)

}
