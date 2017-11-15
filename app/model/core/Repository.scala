package model.core

import javax.inject._

import model.blanks.BlanksTableDefs
import model.ebnf.EbnfExercises
import model.mindmap.MindmapExercises
import model.programming.ProgTableDefs
import model.questions.QuestionsTableDefs
import model.spread.SpreadExercises
import model.sql.SqlTableDefs
import model.uml.UmlTableDefs
import model.web.WebTableDefs
import model.xml.XmlTableDefs
import model.{Course, TableDefs, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class Repository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with TableDefs

    with BlanksTableDefs with EbnfExercises with MindmapExercises with ProgTableDefs
    with QuestionsTableDefs with SpreadExercises with SqlTableDefs
    with UmlTableDefs with WebTableDefs with XmlTableDefs {

  import profile.api._

  def userByName(username: String): Future[Option[User]] = db.run(users.findBy(_.username).apply(username).result.headOption)

  def allUsers: Future[Seq[User]] = db.run(users.result)

  def numOfUsers: Future[Int] = db.run(users.size.result)

  def allCourses: Future[Seq[Course]] = db.run(courses.result)

  def numOfCourses: Future[Int] = db.run(courses.size.result)

}
