package model.programming

import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

case class ProgSample(id: Int, exerciseId: Int, sample: String)

private[model] trait ProgSamples extends ProgExercises {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class ProgSampleTable(tag: Tag) extends Table[ProgSample](tag, "PROG_SAMPLES") {

    def id = column[Int]("ID", O.AutoInc)

    def exerciseId = column[Int]("EXERCISE_ID")

    def sample = column[String]("SAMPLE")

    def pk = primaryKey("PK", (id, exerciseId))

    def exerciseFk = foreignKey("EXERCISE_FK", exerciseId, progExercises)(_.id)


    def * = (id, exerciseId, sample) <> (ProgSample.tupled, ProgSample.unapply)

  }

  lazy val progSamples = TableQuery[ProgSampleTable]

}