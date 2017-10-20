package model.dao

import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class User(id: Long, name: String)

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class UserTable(tag: Tag) extends Table[User](tag, "users") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def * = (id, name) <> ((User.apply _).tupled, User.unapply)

  }

  private val users = TableQuery[UserTable]

  def create(name: String): Future[User] = db.run {
    (users.map(u => u.name)
      returning users.map(_.id)
      into ((name, id) => User(id, name))
      ) += name
  }

  def allUsers: Future[Seq[User]] = db.run(users.result)

}