package model.core

import javax.inject._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class Repository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with model.persistence.FeedbackTableDefs